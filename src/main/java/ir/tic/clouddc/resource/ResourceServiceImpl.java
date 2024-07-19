package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Rack;
import ir.tic.clouddc.center.Room;
import ir.tic.clouddc.event.*;
import ir.tic.clouddc.individual.PersonService;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.utils.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
    public Device validateFormDevice(EventLandingForm eventLandingForm) {
        Optional<Device> currentDevice = getDeviceBySerialNumber(eventLandingForm.getSerialNumber());
        return currentDevice.orElseGet(() -> registerNewDevice(eventLandingForm));
    }

    @Override
    public Device getDeviceDetailModel(long deviceId) {
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        if (optionalDevice.isPresent()) {
            for (Event event : optionalDevice.get().getDeviceStatusEventList()) {
                event.setPersianRegisterDate(UtilService.getFormattedPersianDate(event.getRegisterDate()));
            }
            for (Event event : optionalDevice.get().getDeviceStatusEventList()) {
                event.setPersianRegisterDate(UtilService.getFormattedPersianDate(event.getRegisterDate()));

            }
            for (Event event : optionalDevice.get().getDeviceUtilizerEventList()) {
                event.setPersianRegisterDate(UtilService.getFormattedPersianDate(event.getRegisterDate()));
            }
            for (Event event : optionalDevice.get().getDeviceMovementEventList()) {
                event.setPersianRegisterDate(UtilService.getFormattedPersianDate(event.getRegisterDate()));
            }
            return optionalDevice.get();
        } else {
            throw new NoSuchElementException();
        }
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
        var device = event.getDevice();
        var currentDeviceStatus = getCurrentDeviceStatus(device);
        currentDeviceStatus.setCurrent(false);
        deviceStatusList.add(currentDeviceStatus);

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

    @Override
    public List<Utilizer> getUtilizerListExcept(Utilizer utilizer) {
        return utilizerRepository.findAllByIdNotIn(List.of(utilizer.getId()));
    }

    @Override
    public DeviceStatus getCurrentDeviceStatus(Device device) {
        var currentStatus = deviceStatusRepository.findByDeviceAndCurrent(device, true);
        if (currentStatus.isPresent()) {
            return currentStatus.get();
        } else {
            DeviceStatus defaultDeviceStatus = new DeviceStatus();
            defaultDeviceStatus.setDualPower(true);
            defaultDeviceStatus.setSts(true);
            defaultDeviceStatus.setFan(true);
            defaultDeviceStatus.setModule(true);
            defaultDeviceStatus.setStorage(true);
            defaultDeviceStatus.setPort(true);
            return defaultDeviceStatus;
        }
    }

    private Device registerNewDevice(EventLandingForm eventLandingForm) {

        switch (eventLandingForm.getDevice().getDeviceCategory().getType()) {
            case "Server" -> {   /// Server
                Server srv = new Server();
                srv.setSerialNumber(eventLandingForm.getSerialNumber());
                srv.setLocation(centerService.getLocation(eventLandingForm.getLocationId()).get());
                srv.setUtilizer(getUtilizer(eventLandingForm.getUtilizerId()));
                return deviceRepository.save(srv);
            }
            case "Switch" -> { /// Switch
                Switch sw = new Switch();
                sw.setSerialNumber(eventLandingForm.getSerialNumber());
                sw.setLocation(centerService.getLocation(eventLandingForm.getLocationId()).get());
                sw.setUtilizer(getUtilizer(eventLandingForm.getUtilizerId()));
                return deviceRepository.save(sw);
            }

            case "Firewall" -> { /// Firewall
                Firewall fw = new Firewall();
                fw.setSerialNumber(eventLandingForm.getSerialNumber());
                fw.setLocation(centerService.getLocation(eventLandingForm.getLocationId()).get());
                fw.setUtilizer(getUtilizer(eventLandingForm.getUtilizerId()));
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

