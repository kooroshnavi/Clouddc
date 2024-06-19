package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Center;
import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.resource.Device;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
public class EventLandingForm {
    private short eventCategoryId;
    private String target;
    private int locationId;
    private Location location;
    private short centerId;
    private Center center;
    private short utilizerId;
    private String serialNumber;
    private Device device;
    private String description;
    private MultipartFile file;

    public short getCenterId() {
        return centerId;
    }

    public void setCenterId(short centerId) {
        this.centerId = centerId;
    }

    public void setUtilizerId(short utilizerId) {
        this.utilizerId = utilizerId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public short getEventCategoryId() {
        return eventCategoryId;
    }

    public void setEventCategoryId(short eventCategoryId) {
        this.eventCategoryId = eventCategoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public int getUtilizerId() {
        return utilizerId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

}
