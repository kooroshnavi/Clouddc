package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Device;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DeviceStatusForm {

    private Integer eventCategoryId = 5;

    private Device device;

    private boolean dualPower;  // order 0

    private boolean sts; // order 1

    private boolean fan; // order 2

    private boolean module; // order 3

    private boolean storage; // order 4

    private boolean port; // order 5

    private String description;

    private MultipartFile file;
}
