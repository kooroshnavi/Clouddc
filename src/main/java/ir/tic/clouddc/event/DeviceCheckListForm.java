package ir.tic.clouddc.event;

public class DeviceCheckListForm {

    private boolean dualPower;  // order 0

    private boolean sts; // order 1

    private boolean fan; // order 2

    private boolean module; // order 3

    private boolean storage; // order 4

    private boolean port; // order 5


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
