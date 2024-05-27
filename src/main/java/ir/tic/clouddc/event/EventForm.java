package ir.tic.clouddc.event;

import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
public class EventForm {

    @NotBlank
    private int eventType;

    @NotBlank
    private String description;

    private boolean active;

    private MultipartFile file;

    private long rackId;

    private long salonId;

    private int centerId;

    private int deviceType;

    private int utilizerId;

    private String serialNumber;


    public long getRackId() {
        return rackId;
    }

    public void setRackId(long rackId) {
        this.rackId = rackId;
    }

    public long getSalonId() {
        return salonId;
    }

    public void setSalonId(long salonId) {
        this.salonId = salonId;
    }

    public int getCenterId() {
        return centerId;
    }

    public void setCenterId(int centerId) {
        this.centerId = centerId;
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

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
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


    public void setRackId(int rackId) {
        this.rackId = rackId;
    }


    public void setSalonId(int salonId) {
        this.salonId = salonId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }
}
