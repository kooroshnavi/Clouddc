package ir.tic.clouddc.person;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PersonRegisterForm {

    private String firstName;

    private String lastName;

    @Size(min = 11, max = 11)
    private String phoneNumber;

    private char roleCode;

    private String OTPCode;
}
