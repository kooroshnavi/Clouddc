package ir.tic.clouddc.otp;

import com.github.mfathi91.time.PersianDateTime;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.person.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OTPServiceImpl implements OTPService {

    private static final long LOGIN_EXPIRE_HOUR = 12;

    private static final long REGISTER_EXPIRE_MIN = 10;

    private static final long IP_LIMITED_EXPIRE_HOUR = 100;

    private static final int MAXIMUM_UNREGISTERED_TRIES = 5;

    private final LoadingCache<String, Integer> machineLimitedCache;

    private final LoadingCache<String, String> loginOTPCache;

    private final LoadingCache<String, String> personRegisterOTPCache;

    private final NotificationService notificationService;

    @Autowired
    public OTPServiceImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
        personRegisterOTPCache = CacheBuilder.newBuilder()
                .expireAfterWrite(REGISTER_EXPIRE_MIN, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public String load(String s) {
                        return "";
                    }
                });

        loginOTPCache = CacheBuilder.newBuilder()
                .expireAfterWrite(LOGIN_EXPIRE_HOUR, TimeUnit.HOURS)
                .build(new CacheLoader<>() {
                    @Override
                    public String load(String s) {
                        return "";
                    }
                });

        machineLimitedCache = CacheBuilder.newBuilder()
                .expireAfterAccess(IP_LIMITED_EXPIRE_HOUR, TimeUnit.SECONDS)
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(String s) {
                        return -1;
                    }
                });
    }

    @Override
    public void generateLoginOTP(String address, String otpUid, String expiryTimeUUID, String machine, LocalDateTime requestDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String persianDateTime = formatter.format(PersianDateTime.fromGregorian(requestDateTime));
        String otp = getRandomOTP();
       // log.info(otp);
        loginOTPCache.put(address, otpUid);
        loginOTPCache.put(otpUid, otp);
        loginOTPCache.put(otp, address);
        loginOTPCache.put(expiryTimeUUID, requestDateTime.plusHours(LOGIN_EXPIRE_HOUR).toString());

        notificationService.sendOTPMessage(address, otp, machine, persianDateTime);
    }

    private String getRandomOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }

    public String getOtpUid(String address) throws ExecutionException {
        var otpUid = loginOTPCache.get(address);
        if (otpUid.isBlank()) {
            return "";
        }
        return otpUid;
    }

    @Override
    public String getOtpExpiry(String key) throws ExecutionException {
        var expiryUUID = UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));

        return loginOTPCache.get(expiryUUID.toString());
    }

    @Override
    public String verifyLoginOTP(String otpUid, String providedOtp) throws ExecutionException {
        var realOtp = loginOTPCache.get(otpUid);
        if (realOtp.isBlank()) { //expired or maybe malformed UID
            return "0";
        } else if (!realOtp.equals(providedOtp)) { //invalid otp
            return "-1";
        }

        /* otpCache.invalidate(otpUid);
        otpCache.invalidate(address);
        otpCache.invalidate(UUID.nameUUIDFromBytes(otpUid.getBytes(StandardCharsets.UTF_8)).toString());
        otpCache.invalidate(realOtp);*/

        return loginOTPCache.get(realOtp);
    }

    @Override
    public String generatePersonRegisterOTP(String phoneNumber) throws ExecutionException {
        UUID expiryTimeUUID = UUID.nameUUIDFromBytes(phoneNumber.getBytes(StandardCharsets.UTF_8));
        if (personRegisterOTPCache.get(phoneNumber).isBlank()) {
            String OTPCode = getRandomOTP();
           // log.info(OTPCode);
            personRegisterOTPCache.put(phoneNumber, OTPCode);
            personRegisterOTPCache.put(expiryTimeUUID.toString(), LocalDateTime.now().plusMinutes(REGISTER_EXPIRE_MIN).toString());
            notificationService.sendRegisterOTPMessage(phoneNumber, OTPCode);
        }

        return personRegisterOTPCache.get(expiryTimeUUID.toString());
    }

    @Override
    public String verifyPersonRegisterOTP(String phoneNumber, String otpCode) throws ExecutionException {
        if (personRegisterOTPCache.get(phoneNumber).isBlank()) {
            return "-1";
        } else {
            if (personRegisterOTPCache.get(phoneNumber).equals(otpCode)) {
                personRegisterOTPCache.invalidate(phoneNumber);
                personRegisterOTPCache.invalidate(UUID.nameUUIDFromBytes(phoneNumber.getBytes(StandardCharsets.UTF_8)).toString());

                return "0";
            } else {
                return "403";
            }
        }
    }

    @Override
    public void invalidateLoginOTP(Address address) throws ExecutionException {
        var phoneNumber = address.getValue();
        var uid = loginOTPCache.get(phoneNumber);
        if (!uid.isBlank()) {
            var otp = loginOTPCache.get(uid);
            loginOTPCache.invalidate(uid);
            loginOTPCache.invalidate(phoneNumber);
            loginOTPCache.invalidate(otp);
            loginOTPCache.invalidate(UUID.nameUUIDFromBytes(uid.getBytes(StandardCharsets.UTF_8)).toString());
        }
    }

    @Override
    public String getPersonAddress(String otpUID) throws ExecutionException {
        var key = loginOTPCache.get(otpUID);
        var address = loginOTPCache.get(key);
        if (address.isBlank()) {
            return null;
        }
        return address;
    }

    @Override
    public void verifyUnregisteredIPAddress(String remoteAddr) throws ExecutionException {
        int tries = machineLimitedCache.get(remoteAddr);
        if (tries == -1) {
            machineLimitedCache.put(remoteAddr, 1);
        } else {
            tries += 1;
            machineLimitedCache.put(remoteAddr, tries);
        }
    }

    @Override
    public boolean loginPageAvailability(String remoteAddr) throws ExecutionException {
        return machineLimitedCache.get(remoteAddr) < MAXIMUM_UNREGISTERED_TRIES;
    }
}
