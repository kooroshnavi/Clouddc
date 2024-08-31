package ir.tic.clouddc.otp;

import ir.tic.clouddc.utils.DTOForm;
import lombok.Data;

@Data
public class OtpForm extends DTOForm {

    private char d1;

    private char d2;

    private char d3;

    private char d4;

    private char d5;

    private char d6;

    private String address;
}
