package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Rack;
import ir.tic.clouddc.center.Room;
import ir.tic.clouddc.event.*;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.utils.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final DeviceRepository deviceRepository;

    private final CenterService centerService;

    private final UtilizerRepository utilizerRepository;

    private final LogService logService;

    private final PersonService personService;

    private final DeviceStatusRepository deviceStatusRepository;


    @Autowired
    public ResourceServiceImpl(DeviceRepository deviceRepository, CenterService centerService, UtilizerRepository utilizerRepository, LogService logService, PersonService personService, DeviceStatusRepository deviceStatusRepository) {
        this.deviceRepository = deviceRepository;
        this.centerService = centerService;
        this.utilizerRepository = utilizerRepository;
        this.logService = logService;
        this.personService = personService;
        this.deviceStatusRepository = deviceStatusRepository;
    }

    @Override
    public Device validateFormDevice(EventRegisterForm eventRegisterForm) {
        Optional<Device> currentDevice = getDeviceBySerialNumber(eventRegisterForm.getSerialNumber());
        return currentDevice.orElseGet(() -> registerNewDevice(eventRegisterForm));
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
                event.setPersianRegisterDayTime(UtilService.getFormattedPersianDate(event.getRegisterDate()));
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

    @Override
    public List<Utilizer> getUtilizerList() {
        return utilizerRepository.findAll();
    }


    @Override
    public Optional<Device> getDevice(String serialNumber) {
        return deviceRepository.findBySerialNumber(serialNumber);
    }

    @Override
    public void updateDeviceStatus(DeviceStatusForm deviceStatusForm, DeviceStatusEvent event) {
        List<DeviceStatus> deviceStatusList = new ArrayList<>();
        var device = deviceStatusForm.getDevice();
        var currentDeviceStatus = device.getDeviceStatusList().stream().filter(DeviceStatus::isCurrent).findFirst();
        if (currentDeviceStatus.isPresent()) {
            currentDeviceStatus.get().setCurrent(false);
            deviceStatusList.add(currentDeviceStatus.get());
        }

        DeviceStatus newDeviceStatus = new DeviceStatus();
        newDeviceStatus.setDevice(device);
        newDeviceStatus.setEvent(event);
        newDeviceStatus.setDualPower(deviceStatusForm.isDualPower());
        newDeviceStatus.setSts(deviceStatusForm.isSts());
        newDeviceStatus.setFan(deviceStatusForm.isFan());
        newDeviceStatus.setModule(deviceStatusForm.isModule());
        newDeviceStatus.setStorage(deviceStatusForm.isStorage());
        newDeviceStatus.setPort(deviceStatusForm.isPort());
        newDeviceStatus.setCurrent(true);

        deviceStatusList.add(newDeviceStatus);

        deviceStatusRepository.saveAllAndFlush(deviceStatusList);
    }

    @Override
    public void updateDeviceUtilizer(DeviceUtilizerEvent event) {
        var device = event.getDevice();
        device.setUtilizer(event.getNewUtilizer());
        deviceRepository.saveAndFlush(device);
    }

    @Override
    public void updateDeviceLocation(DeviceMovementEvent event) {
        var device = event.getDevice();
        device.setLocation(event.getDestination());
        if (device.getLocation() instanceof Rack rack) {
            device.setUtilizer(rack.getUtilizer());
        } else if (device.getLocation() instanceof Room room) {
            device.setUtilizer(room.getUtilizer());
        }
        deviceRepository.saveAndFlush(device);
    }

    private Device registerNewDevice(EventRegisterForm eventRegisterForm) {

        switch (eventRegisterForm.getDeviceType()) {
            case 1 -> {   /// Server
                Server srv = new Server();
                srv.setSerialNumber(eventRegisterForm.getSerialNumber());
                srv.setLocation(centerService.getLocation(eventRegisterForm.getLocationId()));
                srv.setUtilizer(getUtilizer(eventRegisterForm.getUtilizerId()));
                var persistence = registerDevicePersistence(4);
                srv.setPersistence(persistence);
                return deviceRepository.save(srv);
            }
            case 2 -> { /// Switch
                Switch sw = new Switch();
                sw.setSerialNumber(eventRegisterForm.getSerialNumber());
                sw.setLocation(centerService.getLocation(eventRegisterForm.getLocationId()));
                sw.setUtilizer(getUtilizer(eventRegisterForm.getUtilizerId()));
                var persistence = registerDevicePersistence(4);
                sw.setPersistence(persistence);
                return deviceRepository.save(sw);
            }

            case 3 -> { /// Firewall
                Firewall fw = new Firewall();
                fw.setSerialNumber(eventRegisterForm.getSerialNumber());
                fw.setLocation(centerService.getLocation(eventRegisterForm.getLocationId()));
                fw.setUtilizer(getUtilizer(eventRegisterForm.getUtilizerId()));
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

