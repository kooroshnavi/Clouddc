package ir.tic.clouddc.otp;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

public interface OtpService {


    String generateOtp(String address, String otpUid, String expireUid, String machine, LocalDateTime localDateTime);

    String getOtpExpiry(String otpUid) throws ExecutionException;

    String getOtpUid(String key) throws ExecutionException;

    String verifyOtp(String otpUid, String requestOtp) throws ExecutionException;

    String getOTP(String otpUid) throws ExecutionException;
}
