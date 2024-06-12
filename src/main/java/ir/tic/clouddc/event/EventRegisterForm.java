package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Device;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
public class EventRegisterForm {

    private short category;

    private short target;

    private String description;

    private boolean active;
    // to update active event
    private int eventId;

    private int locationId;
    // visit event
    private int centerId;
    // device event
    private int deviceType;
    // device event
    private int utilizerId;
    // device event
    private String serialNumber;

    private Device device;
    // device event
    private String title;

    private MultipartFile file;


    public short getTarget() {
        return target;
    }

    public void setTarget(short target) {
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

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public short getCategory() {
        return category;
    }

    public void setCategory(short category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public int getCenterId() {
        return centerId;
    }

    public void setCenterId(int centerId) {
        this.centerId = centerId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getUtilizerId() {
        return utilizerId;
    }

    public void setUtilizerId(int utilizerId) {
        this.utilizerId = utilizerId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
