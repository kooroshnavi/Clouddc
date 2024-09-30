package ir.tic.clouddc.otp;

import ir.tic.clouddc.person.Address;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

public interface OTPService {

    void generateLoginOTP(String address, String otpUid, String expireUid, String machine, LocalDateTime localDateTime);

    String getOtpExpiry(String otpUid) throws ExecutionException;

    String getOtpUid(String key) throws ExecutionException;

    String verifyLoginOTP(String otpUid, String requestOtp) throws ExecutionException;

    String generatePersonRegisterOTP(String phoneNumber) throws ExecutionException;

    String verifyPersonRegisterOTP(String phoneNumber, String otpCode) throws ExecutionException;

    void invalidateLoginOTP(Address address) throws ExecutionException;
}
