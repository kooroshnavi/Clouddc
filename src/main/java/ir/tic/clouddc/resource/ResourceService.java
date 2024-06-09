package ir.tic.clouddc.resource;

import ir.tic.clouddc.event.EventRegisterForm;
import org.springframework.ui.Model;

public interface ResourceService {

    Device validateFormDevice(EventRegisterForm eventRegisterForm);

    Model getDeviceDetailModel(Model model, long deviceId);

    Utilizer getUtilizer(int utilizerId);


}
