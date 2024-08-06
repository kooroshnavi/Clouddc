package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.event.*;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
    public List<DeviceIdSerialCategoryProjection> getLocationDeviceList(Long locationId) {
       return deviceRepository.getDeviceProjection(locationId);
    }

    @Override
    public Device validateFormDevice(EventLandingForm eventLandingForm) {
        Optional<Device> currentDevice = getDeviceBySerialNumber(eventLandingForm.getSerialNumber());
        return currentDevice.orElseGet(() -> {
            try {
                return registerNewDevice(eventLandingForm);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Optional<Device> getDevice(Long deviceId) {
        Optional<Device> currentDevice = deviceRepository.findById(deviceId);

        if (currentDevice.isPresent()) {
            if (currentDevice.get().getDevicePmCatalogList() != null) {
                log.info("Current device has catalog");
                for (DevicePmCatalog devicePmCatalog : currentDevice.get().getDevicePmCatalogList()) {
                    if (devicePmCatalog.isHistory()) {
                        var finishedDate = devicePmCatalog.getLastFinishedDate();
                        devicePmCatalog.setPersianLastFinishedDate(UtilService.getFormattedPersianDate(finishedDate));
                        devicePmCatalog.setPersianLastFinishedDayTime(UtilService.getFormattedPersianDayTime(finishedDate, devicePmCatalog.getLastFinishedTime()));
                    }
                    devicePmCatalog.setPersianNextDue(UtilService.getFormattedPersianDate(devicePmCatalog.getNextDueDate()));
                }
            }
        }

        /*
        for (Event event : device.getDeviceStatusEventList()) {
            event.setPersianRegisterDate(UtilService.getFormattedPersianDate(event.getRegisterDate()));
        }
        for (Event event : device.getDeviceStatusEventList()) {
            event.setPersianRegisterDate(UtilService.getFormattedPersianDate(event.getRegisterDate()));

        }
        for (Event event : device.getDeviceUtilizerEventList()) {
            event.setPersianRegisterDate(UtilService.getFormattedPersianDate(event.getRegisterDate()));
        }
        for (Event event : device.getDeviceMovementEventList()) {
            event.setPersianRegisterDate(UtilService.getFormattedPersianDate(event.getRegisterDate()));
        }*/

        return currentDevice;
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
    public Optional<Device> getDeviceBySerialNumber(String serialNumber) {
        return deviceRepository.findBySerialNumber(serialNumber);
    }

    @Override
    public Device getReferencedDevice(Long deviceId) {
        return deviceRepository.getReferenceById(deviceId);
    }

    @Override
    public void updateDeviceStatus(DeviceStatusForm deviceStatusForm, DeviceStatusEvent event) {
        List<DeviceStatus> deviceStatusList = new ArrayList<>();
        var device = event.getDevice();
        var currentDeviceStatus = getCurrentDeviceStatus(device);
        currentDeviceStatus.setActive(false);
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
        newDeviceStatus.setActive(true);

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
    public void updateDeviceLocation(DeviceMovementEvent event, Utilizer destinationUtilizer) {
        var deviceList = event.getDeviceList();
        var destinationLocation = event.getDestination();
        deviceRepository.updateDeviceLocationAndUtilizer(deviceList, destinationLocation, destinationUtilizer);
    }

    @Override
    public List<UtilizerIdNameProjection> getUtilizerListExcept(Utilizer utilizer) {
        return utilizerRepository.getUtilizerProjectionExcept(List.of(utilizer.getId()));
    }

    @Override
    public DeviceStatus getCurrentDeviceStatus(Device device) {
        var currentStatus = deviceStatusRepository.findByDeviceAndActive(device, true);
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


    private Device registerNewDevice(EventLandingForm eventLandingForm) throws SQLException {

        switch (eventLandingForm.getDevice().getDeviceCategory().getCategory()) {
            case "Server" -> {   /// Server
                Server srv = new Server();
                srv.setSerialNumber(eventLandingForm.getSerialNumber());
                srv.setLocation(centerService.getRefrencedLocation(eventLandingForm.getLocationId()));
                srv.setUtilizer(getUtilizer(eventLandingForm.getUtilizerId()));
                return deviceRepository.save(srv);
            }
            case "Switch" -> { /// Switch
                Switch sw = new Switch();
                sw.setSerialNumber(eventLandingForm.getSerialNumber());
                sw.setLocation(centerService.getRefrencedLocation(eventLandingForm.getLocationId()));
                sw.setUtilizer(getUtilizer(eventLandingForm.getUtilizerId()));
                return deviceRepository.save(sw);
            }

            case "Firewall" -> { /// Firewall
                Firewall fw = new Firewall();
                fw.setSerialNumber(eventLandingForm.getSerialNumber());
                fw.setLocation(centerService.getRefrencedLocation(eventLandingForm.getLocationId()));
                fw.setUtilizer(getUtilizer(eventLandingForm.getUtilizerId()));
                return deviceRepository.save(fw);
            }

            default -> {
                return null;
            }
        }
    }
}

