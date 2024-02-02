package ir.tic.clouddc.notification;

import java.time.LocalDateTime;

public interface NotificationService {

    void sendSuccessLoginMessage(long personId, String ipAddress, LocalDateTime dateTime);

    void sendNewTaskAssignedMessage(String personAddress, String taskTitle, LocalDateTime dateTime);

    void sendActiveTaskAssignedMessage(String personAddress, String taskTitle, int delay, LocalDateTime dateTime);

    void sendScheduleUpdateMessage(String personAddress, String log);

    void sendOTPMessage(String address, String otp, String machine, String date);
}
