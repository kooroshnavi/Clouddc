package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import org.springframework.web.multipart.MultipartFile;

public class LocationStatusForm {

    private short category;

    private Location location;

    private boolean door;  // order 0

    private boolean ventilation; // order 1

    private boolean power; // order 2

    private String description;

    private MultipartFile file;

    public short getCategory() {
        return category;
    }

    public void setCategory(short category) {
        this.category = category;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isDoor() {
        return door;
    }

    public void setDoor(boolean door) {
        this.door = door;
    }

    public boolean isVentilation() {
        return ventilation;
    }

    public void setVentilation(boolean ventilation) {
        this.ventilation = ventilation;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
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
}
