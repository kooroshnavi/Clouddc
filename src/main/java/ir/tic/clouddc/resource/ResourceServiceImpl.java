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

import java.time.YearMonth;
import java.util.*;
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

    private final StorageRepository storageRepository;


    @Autowired
    public ResourceServiceImpl(DeviceRepository deviceRepository, UnassignedDeviceRepository unassignedDeviceRepository, DeviceCategoryRepository deviceCategoryRepository, SupplierRepository supplierRepository, CenterService centerService1, UtilizerRepository utilizerRepository, LogService logService, PersonService personService, ModuleInventoryRepository moduleInventoryRepository, StorageRepository storageRepository) {
        this.deviceRepository = deviceRepository;
        this.unassignedDeviceRepository = unassignedDeviceRepository;
        this.deviceCategoryRepository = deviceCategoryRepository;
        this.supplierRepository = supplierRepository;
        this.centerService = centerService1;
        this.utilizerRepository = utilizerRepository;
        this.logService = logService;
        this.personService = personService;
        this.moduleInventoryRepository = moduleInventoryRepository;
        this.storageRepository = storageRepository;
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
        boolean exists;
        if (resourceType == 1) {
            exists = deviceRepository.existsBySerialNumber(serialNumber);
            boolean UnassignedDeviceExist = unassignedDeviceRepository.existsBySerialNumber(serialNumber);

            return exists || UnassignedDeviceExist;
        } else {
            return storageRepository.existsBySerialNumber(serialNumber);
        }
    }

    @Override
    public void resourceRegister(ResourceRegisterForm resourceRegisterForm, int resourceType) {
        Persistence persistence;
        if (resourceType == 1) {    // Device Register
            UnassignedDevice unassignedDevice = new UnassignedDevice();
            unassignedDevice.setSerialNumber(StringUtils.capitalize(resourceRegisterForm.getSerialNumber()));
            unassignedDevice.setDeviceCategory(deviceCategoryRepository.getReferenceById(resourceRegisterForm.getResourceCategoryId()));
            unassignedDevice.setRemovalDate(UtilService.validateNextDue(UtilService.getDATE().plusDays(7)));
            persistence = new Persistence(UtilService.getDATE(), UtilService.getTime(), personService.getCurrentPerson(), "UnassignedDeviceRegister");
            unassignedDevice.setPersistence(persistence);

            unassignedDeviceRepository.saveAndFlush(unassignedDevice);
        } else {    // Module Register
            var moduleInventory = moduleInventoryRepository.getReferenceById(resourceRegisterForm.getResourceCategoryId());
            if (moduleInventory.getClassification().equals("Storage")) {  // Storage Register
                Storage storage = new Storage(moduleInventory
                        , StringUtils.capitalize(resourceRegisterForm.getSerialNumber())
                        , YearMonth.of(resourceRegisterForm.getMfgYear(), resourceRegisterForm.getMfgMonth())
                        , resourceRegisterForm.getLocale()
                        , true, false, false);
                persistence = new Persistence(UtilService.getDATE(), UtilService.getTime(), personService.getCurrentPerson(), "StorageRegister");
                storage.setPersistence(persistence);
                moduleInventory.setAvailable(moduleInventory.getAvailable() + 1);
                if (moduleInventory.getStorageList() != null) {
                    moduleInventory.getStorageList().add(storage);
                } else {
                    moduleInventory.setStorageList(List.of(storage));
                }

            } else {    // Other Modules
                if (moduleInventory.getPersistence() != null) {
                    logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), "ModuleInventoryUpdate", personService.getCurrentPerson(), moduleInventory.getPersistence());
                } else {
                    persistence = new Persistence(UtilService.getDATE(), UtilService.getTime(), personService.getCurrentPerson(), "ModuleInventoryUpdate");
                    moduleInventory.setPersistence(persistence);
                    moduleInventory.setAvailable(moduleInventory.getAvailable() + resourceRegisterForm.getQty());
                }
            }

            moduleInventoryRepository.saveAndFlush(moduleInventory);
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

        if (!moduleInventoryList.isEmpty()) {
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
        }

        return deviceModuleOverviewMap;  // Tomorrow: this view
    }

    @Override
    public List<ModuleInventory> getModuleCategoryList() {
        return moduleInventoryRepository.findAll();
    }

    @Override
    public List<ModuleInventory> getRelatedModuleInventoryList(Integer categoryId) {
        return moduleInventoryRepository
                .getRelatedModuleInventoryList(categoryId)
                .stream()
                .sorted(Comparator.comparing(ModuleInventory::getAvailable).reversed())
                .toList();
    }

    @Override
    public List<Storage> getRelatedStorageList(Integer specId) {

        return storageRepository.getRelatedStorageList(List.of(specId));
    }

    @Override
    public Map<ModuleInventory, Integer> getDeviceModuleOverview(List<ModulePack> modulePackList) {
        Map<ModuleInventory, Integer> deviceModuleOverviewMap = new HashMap<>();

        if (!modulePackList.isEmpty()) {
            List<Integer> distinctCategoryIdList = modulePackList
                    .stream()
                    .map(ModulePack::getModuleInventory)
                    .map(ModuleInventory::getCategoryId)
                    .distinct()
                    .toList();

            for (Integer categoryId : distinctCategoryIdList) {
                AtomicInteger totalAssigned = new AtomicInteger();
                modulePackList
                        .stream()
                        .filter(modulePack -> modulePack.getModuleInventory().getCategoryId() == categoryId)
                        .map(ModulePack::getQty)
                        .forEach(totalAssigned::addAndGet);

                var inventory = modulePackList
                        .stream()
                        .map(ModulePack::getModuleInventory)
                        .filter(moduleInventory -> moduleInventory.getCategoryId() == categoryId)
                        .findFirst();

                inventory.ifPresent(moduleInventory -> deviceModuleOverviewMap.put(moduleInventory, totalAssigned.get()));
            }
        }

        return deviceModuleOverviewMap;
    }

    @Override
    public List<ModuleInventory> getDeviceCompatibleModuleInventoryList(Integer deviceCategoryID) {
        switch (deviceCategoryID) {
            case 1, 2, 5 -> {
                return moduleInventoryRepository.getDeviceCompatibleModuleInventoryList(List.of(1, 2, 8, 10, 11, 12, 13, 14, 15, 16));
            }
            case 3, 4 -> {
                return moduleInventoryRepository.getDeviceCompatibleModuleInventoryList(List.of(1, 2, 8, 11, 12, 14, 15, 16));
            }
            case 6 -> {
                return moduleInventoryRepository.getDeviceCompatibleModuleInventoryList(List.of(1, 3, 4, 5, 8, 9));
            }
            case 7, 8 -> {
                return moduleInventoryRepository.getDeviceCompatibleModuleInventoryList(List.of(1, 5, 8, 9));
            }
            case 9, 10, 13 -> {
                return moduleInventoryRepository.getDeviceCompatibleModuleInventoryList(List.of(1, 2, 3, 4, 5, 8, 9));
            }
            case 11 -> {
                return moduleInventoryRepository.getDeviceCompatibleModuleInventoryList(List.of(1, 2, 5, 8));
            }
            case 12 -> {
                return moduleInventoryRepository.getDeviceCompatibleModuleInventoryList(List.of(8, 10, 12, 16));
            }
            case 14 -> {
                return moduleInventoryRepository.getDeviceCompatibleModuleInventoryList(List.of(1, 5, 8));
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }

    @Override
    public long updateDeviceModule(DeviceModuleUpdateForm deviceModuleUpdateForm) {
        var device = deviceRepository.getReferenceById(deviceModuleUpdateForm.getDeviceId());
        var moduleInventory = moduleInventoryRepository.getReferenceById(deviceModuleUpdateForm.getModuleInventoryId());
        var updatedValue = deviceModuleUpdateForm.getUpdatedValue();

        List<ModulePack> modulePackList = device.getModulePackList();
        modulePackList
                .stream()
                .filter(modulePack -> modulePack.getModuleInventory() == moduleInventory)
                .findFirst()
                .ifPresentOrElse(modulePack -> modulePack.setQty(modulePack.getQty() + updatedValue), () -> {
                    ModulePack modulePack = new ModulePack();
                    modulePack.setModuleInventory(moduleInventory);
                    modulePack.setQty(updatedValue);
                    modulePack.setDevice(device);
                    if (device.getModulePackList() != null) {
                        device.getModulePackList().add(modulePack);
                    } else {
                        device.setModulePackList(List.of(modulePack));
                    }
                });

        moduleInventory.setAvailable(moduleInventory.getAvailable() + Math.negateExact(updatedValue));

        return deviceRepository.saveAndFlush(device).getId();
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

