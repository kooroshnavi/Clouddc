package ir.tic.clouddc.resource;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResourceRegisterForm {

    @NotNull
    @Size(min = 5, max = 40)
    private String serialNumber;

    @NotNull
    private Integer resourceCategoryId;

    private int qty;

    private long locale;

    private int mfgYear;

    private int mfgMonth;
}
