package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.event.Event;
import ir.tic.clouddc.event.EventForm;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.utils.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final DeviceRepository deviceRepository;

    private final CenterService centerService;

    private final UtilizerRepository utilizerRepository;

    private final LogService logService;

    private final PersonService personService;

    @Autowired
    public ResourceServiceImpl(DeviceRepository deviceRepository, CenterService centerService, UtilizerRepository utilizerRepository, LogService logService, PersonService personService) {
        this.deviceRepository = deviceRepository;
        this.centerService = centerService;
        this.utilizerRepository = utilizerRepository;
        this.logService = logService;
        this.personService = personService;
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

            for (Event event : deviceEventList) {
                event.setPersianDate(UtilService.getFormattedPersianDate(event.getDate()));
            }
            model.addAttribute("deviceEventList", deviceEventList);

            return model;
        }
        return null;
    }

    @Override
    public Utilizer getUtilizer(int utilizerId) {
        Optional<Utilizer> optionalUtilizer = utilizerRepository.findById(utilizerId);
        return optionalUtilizer.orElse(null);
    }

    private Device registerNewDevice(EventForm eventForm) {

        switch (eventForm.getDeviceType()) {
            case 1 -> {   /// Server
                Server srv = new Server();
                srv.setSerialNumber(eventForm.getSerialNumber());
                srv.setLocation(centerService.getLocation(eventForm.getLocationId()));
                srv.setUtilizer(getUtilizer(eventForm.getUtilizerId()));
                var persistence = registerDevicePersistence(4);
                srv.setPersistence(persistence);
                return deviceRepository.save(srv);
            }
            case 2 -> { /// Switch
                Switch sw = new Switch();
                sw.setSerialNumber(eventForm.getSerialNumber());
                sw.setLocation(centerService.getLocation(eventForm.getLocationId()));
                sw.setUtilizer(getUtilizer(eventForm.getUtilizerId()));
                var persistence = registerDevicePersistence(4);
                sw.setPersistence(persistence);
                return deviceRepository.save(sw);
            }

            case 3 -> { /// Firewall
                Firewall fw = new Firewall();
                fw.setSerialNumber(eventForm.getSerialNumber());
                fw.setLocation(centerService.getLocation(eventForm.getLocationId()));
                fw.setUtilizer(getUtilizer(eventForm.getUtilizerId()));
                var persistence = registerDevicePersistence(4);
                fw.setPersistence(persistence);
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

    Persistence registerDevicePersistence(int messageId) {
        var person = personService.getCurrentPerson();
        var persistence = logService.persistenceSetup(person);
        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), messageId, person, persistence);
        return persistence;
    }

}

