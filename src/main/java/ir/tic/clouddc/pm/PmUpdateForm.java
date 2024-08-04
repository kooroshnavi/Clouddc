package ir.tic.clouddc.pm;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Data
public class PmUpdateForm {

    private Long pmId;

    private Integer catalogId;

    private String description;

    private String ownerUsername;

    private Integer actionType;

    private float temperatureValue;

    private MultipartFile file;
}
