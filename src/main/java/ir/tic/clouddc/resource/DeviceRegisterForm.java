package ir.tic.clouddc.resource;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Arrays;

@Data
public class DeviceRegisterForm {

    @NotNull
    @Size(min = 5, max = 40)
    private String serialNumber;

    @NotNull
    private Integer deviceCategoryId;
}
