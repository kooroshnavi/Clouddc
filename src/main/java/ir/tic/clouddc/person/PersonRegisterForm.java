package ir.tic.clouddc.person;

import lombok.Data;

@Data
public class PersonRegisterForm {

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private int roleCode;
}
