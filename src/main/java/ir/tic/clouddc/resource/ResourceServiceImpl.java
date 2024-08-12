package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.event.DeviceCheckList;
import ir.tic.clouddc.event.DeviceStatusForm;
import ir.tic.clouddc.event.EventLandingForm;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final DeviceRepository deviceRepository;

    private final UnassignedDeviceRepository unassignedDeviceRepository;

    private final DeviceCategoryRepository deviceCategoryRepository;

    private final SupplierRepository supplierRepository;

    private final CenterService centerService;

    private final UtilizerRepository utilizerRepository;

    private final LogService logService;

    private final PersonService personService;


    @Autowired
    public ResourceServiceImpl(DeviceRepository deviceRepository, UnassignedDeviceRepository unassignedDeviceRepository, DeviceCategoryRepository deviceCategoryRepository, SupplierRepository supplierRepository, CenterService centerService, UtilizerRepository utilizerRepository, LogService logService, PersonService personService) {
        this.deviceRepository = deviceRepository;
        this.unassignedDeviceRepository = unassignedDeviceRepository;
        this.deviceCategoryRepository = deviceCategoryRepository;
        this.supplierRepository = supplierRepository;
        this.centerService = centerService;
        this.utilizerRepository = utilizerRepository;
        this.logService = logService;
        this.personService = personService;
    }

    @Override
    public List<DeviceIdSerialCategoryVendor_Projection1> getLocationDeviceListProjection(Long locationId) {
        return deviceRepository.getProjection2ForLocationDeviceList(locationId);
    }

    @Override
    public Utilizer getReferencedUtilizer(Integer utilizerId) {
        return utilizerRepository.getReferenceById(utilizerId);
    }

    @Override
    public List<DeviceIdUtilizerId_Projection2> getDeviceProjection2(Long locationId) {
        return deviceRepository.getProjection2List(locationId);
    }

    @Override
    public List<DeviceIdSerialCategoryVendor_Projection1> getNewDeviceList() {
        return unassignedDeviceRepository.getProjection2ForNewDeviceList();
    }

    @Override
    public List<Device> getLocationDeviceList(Long locationId) {
        return deviceRepository.getLocationDeviceList(locationId);
    }

    @Override
    public List<DeviceCategory> getdeviceCategoryList() {
        return deviceCategoryRepository.getList();
    }

    @Override
    public boolean checkDeviceExistence(String serialNumber) {
        boolean deviceExist = deviceRepository.existsBySerialNumber(serialNumber);
        boolean UnassignedDeviceExist = unassignedDeviceRepository.existsBySerialNumber(serialNumber);

        return deviceExist || UnassignedDeviceExist;
    }

    @Override
    public void registerUnassignedDevice(DeviceRegisterForm deviceRegisterForm) {
        UnassignedDevice unassignedDevice = new UnassignedDevice();
        unassignedDevice.setSerialNumber(StringUtils.capitalize(deviceRegisterForm.getSerialNumber()));
        unassignedDevice.setDeviceCategory(deviceCategoryRepository.getReferenceById(deviceRegisterForm.getDeviceCategoryId()));
        unassignedDevice.setRemovalDate(UtilService.getDATE().plusDays(7));

        unassignedDeviceRepository.saveAndFlush(unassignedDevice);
    }

    @Override
    public UnassignedDevice getReferencedUnassignedDevice(Integer unassignedDeviceId) {
        return unassignedDeviceRepository.getReferenceById(unassignedDeviceId);
    }

    @Override
    public Supplier getReferencedDefaultSupplier() {
        return supplierRepository.getReferenceById(1000);
    }

    @Override
    public void deleteUnassignedList(List<Integer> assignedIdList) {
        unassignedDeviceRepository.deleteAllById(assignedIdList);
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
    public Optional<Long> getDeviceIdBySerialNumber(String serialNumber) {
        return deviceRepository.getDeviceIdBySerialNumber(serialNumber);
    }

    @Override
    public Device getReferencedDevice(Long deviceId) throws EntityNotFoundException {
        return deviceRepository.getReferenceById(deviceId);
    }

    @Override
    public void updateDeviceStatus(DeviceStatusForm deviceStatusForm, DeviceCheckList event) {
       /* List<DeviceStatus> deviceStatusList = new ArrayList<>();
      //  var device = event.getDevice();
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

        deviceStatusRepository.saveAllAndFlush(deviceStatusList);*/
    }


    @Override
    public List<UtilizerIdNameProjection> getUtilizerListExcept(List<Integer> utilizerIdList) {
        return utilizerRepository.getUtilizerProjectionExcept(utilizerIdList);
    }

    @Override
    public DeviceStatus getCurrentDeviceStatus(Device device) {
      /*  var currentStatus = deviceStatusRepository.findByDeviceAndActive(device, true);
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
        }*/
        return null;
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

