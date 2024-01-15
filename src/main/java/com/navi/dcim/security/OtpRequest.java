package com.navi.dcim.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OtpRequest {

    @Size(min = 11, max = 11)
    @NotBlank
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
