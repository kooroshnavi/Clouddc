package ir.tic.clouddc.otp;

import ir.tic.clouddc.person.Address;
import ir.tic.clouddc.person.Person;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface OTPService {

    void generateLoginOTP(String address, String otpUid, String expireUid, String machine, LocalDateTime localDateTime);

    String getOtpExpiry(String otpUid) throws ExecutionException;

    String getOtpUid(String key) throws ExecutionException;

    String verifyLoginOTP(String otpUid, String requestOtp) throws ExecutionException;

    String generatePersonRegisterOTP(String phoneNumber) throws ExecutionException;

    String verifyPersonRegisterOTP(String phoneNumber, String otpCode) throws ExecutionException;

    void invalidateLoginOTP(Address address) throws ExecutionException;

    String getPersonAddress(String otpUID) throws ExecutionException;

    void verifyUnregisteredIPAddress(String remoteAddr) throws ExecutionException;

    boolean loginPageAvailability(String remoteAddr) throws ExecutionException;

    void refreshPersonBackupCodeList(Person currentPerson);

    List<BackupCodeRepository.BackupCodeProjection> getBackCodeList(Person currentPerson);
}

