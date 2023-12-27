package com.navi.dcim.notification;

import java.time.LocalDateTime;

public interface NotificationService {

    void sendSuccessLoginMessage(String personAddress, String ipAddress, LocalDateTime dateTime);

    void sendNewTaskAssignedMessage(String personAddress, String taskTitle, LocalDateTime dateTime);

    void sendActiveTaskAssignedMessage(String personAddress, String taskTitle, int delay, LocalDateTime dateTime);

    void sendScheduleUpdateMessage(String personAddress, String log);
}
