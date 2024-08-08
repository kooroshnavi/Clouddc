package ir.tic.clouddc.resource;

import ir.tic.clouddc.event.DeviceCheckList;
import ir.tic.clouddc.event.DeviceStatusForm;
import ir.tic.clouddc.event.EventLandingForm;

import java.util.List;
import java.util.Optional;

public interface ResourceService {

    List<DeviceIdSerialCategory_Projection1> getLocationDeviceList(Long locationId);

    Utilizer getReferencedUtilizer(Integer utilizerId);

    List<DeviceIdUtilizerId_Projection2> getDeviceProjection2(Long locationId);

    List<DeviceIdSerialCategory_Projection1> getNewDeviceList();

    interface DeviceIdSerialCategory_Projection1 {
        Long getId();

        String getSerialNumber();

        String getCategory();

        String getModel();
    }

    interface DeviceIdUtilizerId_Projection2 {
        Long getDeviceId();

        Integer getDeviceUtilizerId();
    }

    interface UtilizerIdNameProjection {
        Integer getId();

        String getName();
    }


    Device validateFormDevice(EventLandingForm eventLandingForm);

    Optional<Device> getDevice(Long deviceId);

    Optional<Device> getDeviceBySerialNumber(String serialNumber);

    Device getReferencedDevice(Long deviceId);

    Utilizer getUtilizer(int utilizerId);

    void updateDeviceStatus(DeviceStatusForm deviceStatusForm, DeviceCheckList event);

    void updateDeviceUtilizer(List<Long> deviceIdList, Utilizer utilizer);

    List<UtilizerIdNameProjection> getUtilizerListExcept(List<Integer> utilizerIdList);

    DeviceStatus getCurrentDeviceStatus(Device device);

}
