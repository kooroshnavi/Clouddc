package ir.tic.clouddc.resource;

import ir.tic.clouddc.event.EventRegisterForm;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

public interface ResourceService {

    Device validateFormDevice(EventRegisterForm eventRegisterForm);

    Model getDeviceDetailModel(Model model, long deviceId);

    Utilizer getUtilizer(int utilizerId);
    List<Utilizer> getUtilizerList();

    Optional<DeviceStatus> getCurrentDeviceHealthStatus();

    Optional<Device> getDevice(String serialNumber);


}
