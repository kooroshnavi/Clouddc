package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Device;
import org.springframework.web.multipart.MultipartFile;

public class DeviceStatusForm {

    private short eventCategoryId = 5;

    private Device device;

    private boolean dualPower;  // order 0

    private boolean sts; // order 1

    private boolean fan; // order 2

    private boolean module; // order 3

    private boolean storage; // order 4

    private boolean port; // order 5

    private String description;

    private MultipartFile file;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
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

    public short getEventCategoryId() {
        return eventCategoryId;
    }

    public void setEventCategoryId(short eventCategoryId) {
        this.eventCategoryId = eventCategoryId;
    }

    public boolean isDualPower() {
        return dualPower;
    }

    public void setDualPower(boolean dualPower) {
        this.dualPower = dualPower;
    }

    public boolean isSts() {
        return sts;
    }

    public void setSts(boolean sts) {
        this.sts = sts;
    }

    public boolean isFan() {
        return fan;
    }

    public void setFan(boolean fan) {
        this.fan = fan;
    }

    public boolean isModule() {
        return module;
    }

    public void setModule(boolean module) {
        this.module = module;
    }

    public boolean isStorage() {
        return storage;
    }

    public void setStorage(boolean storage) {
        this.storage = storage;
    }

    public boolean isPort() {
        return port;
    }

    public void setPort(boolean port) {
        this.port = port;
    }
}
