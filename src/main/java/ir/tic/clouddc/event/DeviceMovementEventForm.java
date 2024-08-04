package ir.tic.clouddc.event;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class DeviceMovementEventForm {

    private List<Long> deviceIdList;

    private Long sourceLocId;

    private Long destLocId;

    private String description;

    private MultipartFile multipartFile;
}
