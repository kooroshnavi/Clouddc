package ir.tic.clouddc.notification;

import java.time.LocalDateTime;

public interface NotificationService {

    void sendSuccessLoginMessage(String personAddress, String ipAddress, LocalDateTime dateTime);

    void sendPmAssignedMessage(String personAddress, String taskTitle, LocalDateTime dateTime);

    void sendScheduleUpdateMessage(String personAddress, String log);

    void sendOTPMessage(String address, String otp, String machine, String date);

    void sendExceptionMessage(String message, LocalDateTime dateTime);

    void sendRegisterOTPMessage(String phoneNumber, String otpCode);

    void sendPersonWelcomingMessage(String personName ,String phoneNumber, char role);
}
