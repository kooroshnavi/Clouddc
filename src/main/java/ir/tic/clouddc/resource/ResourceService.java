package ir.tic.clouddc.resource;

import ir.tic.clouddc.event.*;
import org.springframework.ui.Model;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ResourceService {

    Device validateFormDevice(EventLandingForm eventLandingForm);

    Device getDeviceDetailModel(long deviceId);

    Utilizer getUtilizer(int utilizerId);
    List<Utilizer> getUtilizerList();
    void updateDeviceStatus(DeviceStatusForm deviceStatusForm, DeviceStatusEvent event);

    void updateDeviceUtilizer(DeviceUtilizerEvent event);

    void updateDeviceLocation(DeviceMovementEvent event);

    List<Utilizer> getUtilizerListExcept(Utilizer utilizer);

    DeviceStatus getCurrentDeviceStatus(Device device);

    Device getDevice(Long deviceId) throws SQLException;
    Optional<Device> getDevice(String serialNumber);
}
