package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.event.DeviceCheckList;
import ir.tic.clouddc.event.DeviceStatusForm;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final ModuleInventoryRepository moduleInventoryRepository;


    @Autowired
    public ResourceServiceImpl(DeviceRepository deviceRepository, UnassignedDeviceRepository unassignedDeviceRepository, DeviceCategoryRepository deviceCategoryRepository, SupplierRepository supplierRepository, CenterService centerService1, UtilizerRepository utilizerRepository, LogService logService, PersonService personService, ModuleInventoryRepository moduleInventoryRepository) {
        this.deviceRepository = deviceRepository;
        this.unassignedDeviceRepository = unassignedDeviceRepository;
        this.deviceCategoryRepository = deviceCategoryRepository;
        this.supplierRepository = supplierRepository;
        this.centerService = centerService1;
        this.utilizerRepository = utilizerRepository;
        this.logService = logService;
        this.personService = personService;
        this.moduleInventoryRepository = moduleInventoryRepository;
    }

    @Override
    public List<DeviceIdSerialCategoryVendor_Projection1> getLocationDeviceListProjection(Long locationId) {
        return deviceRepository.getProjection2ForLocationDeviceList(locationId);
    }

    @Override
    public Utilizer getReferencedUtilizer(Integer utilizerId) throws EntityNotFoundException {
        return utilizerRepository.getReferenceById(utilizerId);
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
    public boolean checkResourceExistence(String serialNumber, int resourceType) {
      /*  boolean exists;
        if (resourceType == 1) {
            exists = deviceRepository.existsBySerialNumber(serialNumber);
            boolean UnassignedDeviceExist = unassignedDeviceRepository.existsBySerialNumber(serialNumber);

            return exists || UnassignedDeviceExist;
        } else {
            exists = moduleRepository.existsBySerialNumber(serialNumber);

            return exists;
        }*/
        return true;
    }

    @Override
    public void resourceRegister(ResourceRegisterForm resourceRegisterForm, int resourceType) {
        Persistence persistence;
        if (resourceType == 1) {
            UnassignedDevice unassignedDevice = new UnassignedDevice();
            unassignedDevice.setSerialNumber(StringUtils.capitalize(resourceRegisterForm.getSerialNumber()));
            unassignedDevice.setDeviceCategory(deviceCategoryRepository.getReferenceById(resourceRegisterForm.getResourceCategoryId()));
            unassignedDevice.setRemovalDate(UtilService.validateNextDue(UtilService.getDATE().plusDays(7)));
            persistence = new Persistence(UtilService.getDATE(), UtilService.getTime(), personService.getCurrentPerson(), "UnassignedDeviceRegister");
            unassignedDevice.setPersistence(persistence);

            unassignedDeviceRepository.saveAndFlush(unassignedDevice);
        } else {
            /*
            Module module = new Module();
            module.setModuleCategory(moduleCategoryRepository.getReferenceById(resourceRegisterForm.getResourceCategoryId()));
            module.setSerialNumber(resourceRegisterForm.getSerialNumber());
            module.setSpare(true);
            module.setActive(false);
            module.setProblematic(false);
            persistence = new Persistence(UtilService.getDATE(), UtilService.getTime(), personService.getCurrentPerson(), "ModuleRegister");
            module.setPersistence(persistence);
            if (resourceRegisterForm.getLocale() == 1) {
                module.setLocalityId(CenterService.ROOM_1_ID);
            } else if (resourceRegisterForm.getLocale() == 2) {
                module.setLocalityId(CenterService.ROOM_2_ID);
            } else if (resourceRegisterForm.getLocale() == 3) {
                module.setLocalityId(CenterService.ROOM_412_ID);
            } else {
                module.setLocalityId(0);
            }

            moduleRepository.saveAndFlush(module);*/
        }
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
    public void deleteUnassignedDeviceIdList(List<Integer> assignedIdList) {
        unassignedDeviceRepository.deleteAllById(assignedIdList);
    }

    @Override
    public boolean newDevicePresentCheck() {
        var count = unassignedDeviceRepository.count();
        return count != 0;
    }

    @Override
    public String scheduleUnassignedDeviceRemoval() {
        List<Integer> unassignedDeviceIdList = unassignedDeviceRepository.getUnassignedDeviceIdList(UtilService.getDATE());
        if (!unassignedDeviceIdList.isEmpty()) {
            unassignedDeviceRepository.deleteAllById(unassignedDeviceIdList);

            return unassignedDeviceIdList.size() + " un-assigned device removed.";
        }
        return "No device were removed.";
    }

    @Override
    public List<Utilizer> getUtilierList() {
        return utilizerRepository.fetchGenuineList();
    }

    @Override
    public Map<ModuleInventory, Integer> getModuleOverviewMap() {
        Map<ModuleInventory, Integer> deviceModuleOverviewMap = new HashMap<>();
        List<ModuleInventory> moduleInventoryList = moduleInventoryRepository.getAvailableList();

        List<Integer> distinctList = moduleInventoryList
                .stream()
                .map(ModuleInventory::getCategoryId)
                .distinct()
                .toList();

        for (Integer categoryId : distinctList) {
            AtomicInteger totalAvailable = new AtomicInteger();
            moduleInventoryList
                    .stream()
                    .filter(moduleInventory -> moduleInventory.getCategoryId() == categoryId)
                    .map(ModuleInventory::getAvailable)
                    .forEach(totalAvailable::addAndGet);

            var category = moduleInventoryList
                    .stream()
                    .filter(moduleInventory -> moduleInventory.getCategoryId() == categoryId)
                    .findFirst();

            category.ifPresent(moduleInventory -> deviceModuleOverviewMap.put(moduleInventory, totalAvailable.get()));
        }

        return deviceModuleOverviewMap;  // Tomorrow: this view
    }

    @Override
    public List<ModuleInventory> getModuleCategoryList() {
        return moduleInventoryRepository.findAll();
    }

    @Override
    public Optional<Device> getDevice(Long deviceId) {
        Optional<Device> currentDevice = deviceRepository.findById(deviceId);

        if (currentDevice.isPresent()) {
            if (currentDevice.get().getDevicePmCatalogList() != null) {
                log.info("Current device has catalog");
                for (DevicePmCatalog devicePmCatalog : currentDevice.get().getDevicePmCatalogList()) {
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
}

