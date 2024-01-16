package com.navi.dcim.otp;

import com.navi.dcim.person.Address;

import java.time.LocalDateTime;

public interface OtpService {


    void sendOtpMessage(Address address, String machine, LocalDateTime localDateTime);
}
