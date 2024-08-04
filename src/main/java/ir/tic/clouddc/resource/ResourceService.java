package ir.tic.clouddc.resource;

import ir.tic.clouddc.event.*;

import java.util.List;
import java.util.Optional;

public interface ResourceService {

    List<DeviceIdSerialCategoryProjection> getLocationDeviceList(Long locationId);

    interface DeviceIdSerialCategoryProjection {
        Long getId();
        String getSerialNumber();
        String getCategory();
        String getModel();
    }


    Device validateFormDevice(EventLandingForm eventLandingForm);

    Optional<Device> getDevice(Long deviceId);

    Optional<Device> getDeviceBySerialNumber(String serialNumber);

    Device getReferencedDevice(Long deviceId);

    Utilizer getUtilizer(int utilizerId);
    List<Utilizer> getUtilizerList();
    void updateDeviceStatus(DeviceStatusForm deviceStatusForm, DeviceStatusEvent event);

    void updateDeviceUtilizer(DeviceUtilizerEvent event);

    void updateDeviceLocation(DeviceMovementEvent event);

    List<Utilizer> getUtilizerListExcept(Utilizer utilizer);

    DeviceStatus getCurrentDeviceStatus(Device device);
    
}
