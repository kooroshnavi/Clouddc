package ir.tic.clouddc.resource;

import ir.tic.clouddc.event.EventForm;
import org.springframework.ui.Model;

public interface ResourceService {

    Device validateFormDevice(EventForm eventForm);

    Model getDeviceDetailModel(Model model, long deviceId);


}
