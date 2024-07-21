package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Center;
import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.resource.Device;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EventLandingForm {
    private int eventCategoryId;
    private String target;
    private Long locationId;
    private Location location;
    private short centerId;
    private Center center;
    private short utilizerId;
    private String serialNumber;
    private Device device;
    private String description;
    private MultipartFile file;
}
