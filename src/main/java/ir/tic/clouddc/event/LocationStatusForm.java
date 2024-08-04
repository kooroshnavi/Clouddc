package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.LocationStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class LocationStatusForm {

    private Integer eventCategoryId = 2;

    private Location location;

    private boolean door;  // order 0

    private boolean ventilation; // order 1

    private boolean power; // order 2

    private String description;

    private MultipartFile file;

    private LocationStatus currentLocationStatus;
}
