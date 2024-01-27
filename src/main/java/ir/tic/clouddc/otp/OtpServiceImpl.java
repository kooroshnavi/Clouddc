package ir.tic.clouddc.otp;

import com.github.mfathi91.time.PersianDateTime;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import ir.tic.clouddc.notification.NotificationService;
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
public class OtpServiceImpl implements OtpService {

    private static final long EXPIRE_HOUR = 12;

    private LoadingCache<String, String> otpCache;

    private final NotificationService notificationService;

    @Autowired
    public OtpServiceImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
        otpCache = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRE_HOUR, TimeUnit.HOURS)
                .build(new CacheLoader<>() {
                    @Override
                    public String load(String s) {
                        return "";
                    }
                });
    }

    @Override
    public void generateOtp(String address, String otpUid, String expiryTimeUUID, String machine, LocalDateTime requestDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String persianDateTime = formatter.format(PersianDateTime.fromGregorian(requestDateTime));
        String otp = getRandomOTP(otpUid);
        otpCache.put(address, otpUid);
        otpCache.put(otpUid, otp);
        otpCache.put(otp, address);
        otpCache.put(expiryTimeUUID, requestDateTime.plusHours(EXPIRE_HOUR).toString());
        notificationService.sendOTPMessage(address, otp, machine, persianDateTime);
    }

    private String getRandomOTP(String otpUid) {
        String otp = new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
        return otp;
    }

    public String getOtpUid(String address) throws ExecutionException {
        var otpUid = otpCache.get(address);
        if (otpUid.isBlank()) {
            return "";
        }
        return otpUid;
    }

    @Override
    public String getOtpExpiry(String otpUid) throws ExecutionException {
        var expiryUUID = UUID.nameUUIDFromBytes(otpUid.getBytes(StandardCharsets.UTF_8));
        return otpCache.get(expiryUUID.toString());
    }

    @Override
    public String verifyOtp(String otpUid, String providedOtp) throws ExecutionException {
        var realOtp = otpCache.get(otpUid);
        if (realOtp.isBlank()) { //expired or maybe malformed UID
            return "0";
        } else if (!realOtp.equals(providedOtp)) { //invalid otp
            return "-1";
        }
        var address = otpCache.get(realOtp);
       /* otpCache.invalidate(otpUid);
        otpCache.invalidate(address);
        otpCache.invalidate(UUID.nameUUIDFromBytes(otpUid.getBytes(StandardCharsets.UTF_8)).toString());
        otpCache.invalidate(realOtp);*/

        return address;
    }


}
