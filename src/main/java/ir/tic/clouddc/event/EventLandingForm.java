package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Center;
import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.resource.Device;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EventLandingForm {
    private Integer eventCategoryId;
    private String target;
    private Long locationId;
    private Location location;
    private Integer centerId;
    private Center center;
    private Integer utilizerId;
    private String serialNumber;
    private Device device;
    private String description;
    private MultipartFile file;
}
