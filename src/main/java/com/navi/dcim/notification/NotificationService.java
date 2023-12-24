package com.navi.dcim.notification;

import com.navi.dcim.person.Person;

import java.time.LocalDateTime;

public interface NotificationService {

    void sendSuccessLoginMessage(Person person, String ipAddress, LocalDateTime dateTime);
}
