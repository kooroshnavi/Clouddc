package ir.tic.clouddc.pm;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Data
public class PmInterfaceRegisterForm {

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
