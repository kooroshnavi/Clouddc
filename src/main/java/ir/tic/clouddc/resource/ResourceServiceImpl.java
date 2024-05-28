package ir.tic.clouddc.resource;

import ir.tic.clouddc.event.Event;
import ir.tic.clouddc.event.EventForm;
import ir.tic.clouddc.utils.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.util.Optional;

public class ResourceServiceImpl implements ResourceService {

    private final DeviceRepository deviceRepository;

    @Autowired
    public ResourceServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Device validateFormDevice(EventForm eventForm) {
        Optional<Device> currentDevice = getDeviceBySerialNumber(eventForm.getSerialNumber());
        return currentDevice.orElseGet(() -> registerNewDevice(eventForm));
    }

    @Override
    public Model getDeviceDetailModel(Model model, long deviceId) {
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        if (optionalDevice.isPresent()) {
            var baseDevice = optionalDevice.get();
            var deviceEventList = baseDevice.getEventList();
            if (baseDevice instanceof Server device) {
                model.addAttribute("device", device);
            } else if (baseDevice instanceof Switch device) {
                model.addAttribute("device", device);
            } else if (baseDevice instanceof Firewall device) {
                model.addAttribute("device", device);
            }

            for (Event event : deviceEventList){
                event.setPersianDate(UtilService.getFormattedPersianDate(event.getDate()));
            }
            model.addAttribute("deviceEventList", deviceEventList);

            return model;
        }
        return null;
    }

    private Device registerNewDevice(EventForm eventForm) {

        switch (eventForm.getDeviceType()) {
            case 1 -> {   /// Server
                Server srv = new Server();
                srv.setSerialNumber(eventForm.getSerialNumber());
                return deviceRepository.save(srv);
            }
            case 2 -> { /// Switch
                Switch sw = new Switch();
                sw.setSerialNumber(eventForm.getSerialNumber());
                return deviceRepository.save(sw);
            }

            case 3 -> { /// Firewall
                Firewall fw = new Firewall();
                fw.setSerialNumber(eventForm.getSerialNumber());
                return deviceRepository.save(fw);
            }

            default -> {
                return null;
            }
        }
    }

    private Optional<Device> getDeviceBySerialNumber(String serialNumber) {
        return deviceRepository.findBySerialNumber(serialNumber);
    }

}

