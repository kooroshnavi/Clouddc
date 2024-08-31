package ir.tic.clouddc.otp;

import ir.tic.clouddc.utils.DTOForm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OtpRequest extends DTOForm {

    @Size(min = 11, max = 11)
    @NotBlank
    private String address;
}
