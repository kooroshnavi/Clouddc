package ir.tic.clouddc.pm;

import ir.tic.clouddc.utils.DTOForm;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Data
public class PmInterfaceRegisterForm extends DTOForm {

    private Integer pmInterfaceId;

    @NotBlank(message = "عنوان نباید خالی باشد")
    private String title;

    @NotBlank(message = "توضیحات نباید خالی باشد")
    private String description;

    private int period;

    private int target;

    private boolean enabled;

    private boolean update;

    private MultipartFile file;
}
