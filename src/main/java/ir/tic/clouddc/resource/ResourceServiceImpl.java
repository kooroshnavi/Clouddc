package ir.tic.clouddc.resource;

import ir.tic.clouddc.event.EventForm;
import org.springframework.beans.factory.annotation.Autowired;

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

    private Device registerNewDevice(EventForm eventForm) {

        switch (eventForm.getDeviceType()) {
            case 1 -> {   /// Server
                Server srv = new Server();
                srv.setSerialNumber(eventForm.getSerialNumber());
                srv.setType("Server");
                return deviceRepository.save(srv);
            }
            case 2 -> { /// Switch
                Switch sw = new Switch();
                sw.setSerialNumber(eventForm.getSerialNumber());
                sw.setType("Switch");
                return deviceRepository.save(sw);
            }

            case 3 -> { /// Firewall
                Firewall fw = new Firewall();
                fw.setSerialNumber(eventForm.getSerialNumber());
                fw.setType("Firewall");
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

