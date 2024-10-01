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

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Map.entry;

@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final DeviceRepository deviceRepository;

    private final UnassignedDeviceRepository unassignedDeviceRepository;

    private final DeviceCategoryRepository deviceCategoryRepository;

    private final SupplierRepository supplierRepository;

    private final UtilizerRepository utilizerRepository;

    private final LogService logService;

    private final PersonService personService;

    private final ModuleInventoryRepository moduleInventoryRepository;

    private final StorageRepository storageRepository;


    @Autowired
    public ResourceServiceImpl(DeviceRepository deviceRepository, UnassignedDeviceRepository unassignedDeviceRepository, DeviceCategoryRepository deviceCategoryRepository, SupplierRepository supplierRepository, UtilizerRepository utilizerRepository, LogService logService, PersonService personService, ModuleInventoryRepository moduleInventoryRepository, StorageRepository storageRepository) {
        this.deviceRepository = deviceRepository;
        this.unassignedDeviceRepository = unassignedDeviceRepository;
        this.deviceCategoryRepository = deviceCategoryRepository;
        this.supplierRepository = supplierRepository;
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
            persistence = new Persistence(UtilService.getDATE(), UtilService.getTime(), personService.getCurrentPerson(), "UnassignedDeviceRegister", "DeviceRegister");
            unassignedDevice.setPersistence(persistence);

            unassignedDeviceRepository.saveAndFlush(unassignedDevice);
        } else {    // Module Register
            var moduleInventory = moduleInventoryRepository.getReferenceById(resourceRegisterForm.getResourceCategoryId());
            if (moduleInventory.getClassificationId() == 6) {  // Storage Register
                Storage storage = new Storage(moduleInventory
                        , StringUtils.capitalize(resourceRegisterForm.getSerialNumber())
                        , YearMonth.of(resourceRegisterForm.getMfgYear(), resourceRegisterForm.getMfgMonth())
                        , resourceRegisterForm.getLocale()
                        , true, false, false);
                persistence = new Persistence(UtilService.getDATE(), UtilService.getTime(), personService.getCurrentPerson(), "StorageRegister", "StorageRegister");
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
                    persistence = new Persistence(UtilService.getDATE(), UtilService.getTime(), personService.getCurrentPerson(), "ModuleInventoryUpdate", "ModuleRegister");
                    moduleInventory.setPersistence(persistence);
                }
                moduleInventory.setAvailable(moduleInventory.getAvailable() + resourceRegisterForm.getQty());
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

                var inventory = moduleInventoryList
                        .stream()
                        .filter(moduleInventory -> moduleInventory.getCategoryId() == categoryId)
                        .findFirst();

                inventory.ifPresent(moduleInventory -> deviceModuleOverviewMap.put(moduleInventory, totalAvailable.get()));
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
    public List<Storage> getRelatedSpareStorageList(Integer inventoryId) {
        return storageRepository.getRelatedSpareStorageList(List.of(inventoryId), false);
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

                if (totalAssigned.get() > 0) {
                    var inventory = modulePackList
                            .stream()
                            .map(ModulePack::getModuleInventory)
                            .filter(moduleInventory -> moduleInventory.getCategoryId() == categoryId)
                            .findFirst();

                    inventory.ifPresent(moduleInventory -> deviceModuleOverviewMap.put(moduleInventory, totalAssigned.get()));
                }
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
    public long updateDeviceModule(ModuleUpdateForm moduleUpdateForm) {
        var device = deviceRepository.getReferenceById(moduleUpdateForm.getDeviceId());

        if (moduleUpdateForm.isStorageUpdate()) {    // Storage Update
            List<Long> currentStorageIdList = storageRepository.getDeviceStorageIdList(device.getId());
            List<Long> alterStorageIdList = getUpdatableStorageIdList(moduleUpdateForm, currentStorageIdList);
            if (!alterStorageIdList.isEmpty()) {
                for (Long storageId : alterStorageIdList) {
                    Storage storage = storageRepository.getReferenceById(storageId);
                    if (storage.isSpare()) {
                        storage.setSpare(false);
                        storage.setLocalityId(device.getId());
                        updateStorageModulePackAndInventory(device, storage.getModuleInventory(), false);
                    } else {
                        storage.setSpare(true);
                        storage.setLocalityId(CenterService.ROOM_1_ID);
                        updateStorageModulePackAndInventory(device, storage.getModuleInventory(), true);
                    }
                }
            }

        } else {  // other modules update
            var moduleInventory = moduleInventoryRepository.getReferenceById(moduleUpdateForm.getModuleInventoryId());
            var updatedValue = moduleUpdateForm.getUpdatedValue();
            List<ModulePack> modulePackList = device.getModulePackList();
            modulePackList
                    .stream()
                    .filter(modulePack -> modulePack.getModuleInventory() == moduleInventory)
                    .findFirst()
                    .ifPresentOrElse(modulePack -> {
                        modulePack.setQty(modulePack.getQty() + updatedValue);
                        modulePack.getPackHistory().put(LocalDateTime.now(), updatedValue);
                        if (updatedValue > 0) {
                            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("increaseDeviceModule"), personService.getCurrentPerson(), modulePack.getPersistence());
                        } else {
                            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("decreaseDeviceModule"), personService.getCurrentPerson(), modulePack.getPersistence());
                        }
                    }, () -> {
                        ModulePack modulePack = new ModulePack();
                        modulePack.setModuleInventory(moduleInventory);
                        modulePack.setQty(updatedValue);
                        modulePack.setDevice(device);
                        modulePack.setPackHistory((Map.ofEntries(entry(LocalDateTime.now(), updatedValue))));
                        Persistence persistence = logService.persistenceSetup(personService.getCurrentPerson(), "DeviceModuleUpdate");
                        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("increaseDeviceModule"), personService.getCurrentPerson(), persistence);
                        modulePack.setPersistence(persistence);

                        if (device.getModulePackList() != null) {
                            device.getModulePackList().add(modulePack);
                        } else {
                            device.setModulePackList(List.of(modulePack));
                        }
                    });

            moduleInventory.setAvailable(moduleInventory.getAvailable() + Math.negateExact(updatedValue));
        }

        return deviceRepository.saveAndFlush(device).getId();
    }

    private void updateStorageModulePackAndInventory(Device device, ModuleInventory moduleInventory, boolean spare) {
        if (spare) {
            moduleInventory.setAvailable(moduleInventory.getAvailable() + 1);
            device
                    .getModulePackList()
                    .stream()
                    .filter(modulePack -> modulePack.getModuleInventory() == moduleInventory)
                    .findFirst()
                    .ifPresent(modulePack -> {
                        modulePack.setQty(modulePack.getQty() - 1);
                        modulePack.getPackHistory().put(LocalDateTime.now(), Math.negateExact(1));
                        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("decreaseDeviceStorage"), personService.getCurrentPerson(), modulePack.getPersistence());
                    });
        } else {
            moduleInventory.setAvailable(moduleInventory.getAvailable() - 1);
            device
                    .getModulePackList()
                    .stream()
                    .filter(modulePack -> modulePack.getModuleInventory() == moduleInventory)
                    .findFirst()
                    .ifPresentOrElse(modulePack -> {
                        modulePack.setQty(modulePack.getQty() + 1);
                        modulePack.getPackHistory().put(LocalDateTime.now(), 1);
                        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("increaseDeviceStorage"), personService.getCurrentPerson(), modulePack.getPersistence());
                    }, () -> {
                        ModulePack modulePack = new ModulePack();
                        modulePack.setModuleInventory(moduleInventory);
                        modulePack.setQty(1);
                        modulePack.setDevice(device);
                        modulePack.setPackHistory((Map.ofEntries(entry(LocalDateTime.now(), 1))));
                        Persistence persistence = logService.persistenceSetup(personService.getCurrentPerson(), "DeviceModuleUpdate");
                        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("increaseDeviceStorage"), personService.getCurrentPerson(), persistence);
                        modulePack.setPersistence(persistence);
                        if (device.getModulePackList() != null) {
                            device.getModulePackList().add(modulePack);
                        } else {
                            device.setModulePackList(List.of(modulePack));
                        }
                    });
        }
    }

    private static List<Long> getUpdatableStorageIdList(ModuleUpdateForm moduleUpdateForm, List<Long> currentStorageIdList) {
        List<Long> alterStorageIdList = new ArrayList<>();
        for (Long storageId : currentStorageIdList) {
            if (!moduleUpdateForm.getStorageIdList().contains(storageId)) {
                alterStorageIdList.add(storageId);
            }
        }
        for (Long storageId : moduleUpdateForm.getStorageIdList()) {
            if (!currentStorageIdList.contains(storageId)) {
                alterStorageIdList.add(storageId);
            }
        }

        return alterStorageIdList;
    }

    @Override
    public List<Storage> getDeviceAssignedAndSpareStorageList(long deviceId, List<ModuleInventory> compatibleStorageInventoryList) {
        return storageRepository
                .fetchDeviceAssignedAndSpareList(deviceId, compatibleStorageInventoryList, false)
                .stream()
                .sorted(Comparator.comparing(Storage::isSpare))
                .sorted(Comparator.comparing(storage -> storage.getModuleInventory().getCategoryId()))
                .toList();
    }

    @Override
    public void inventoryUpdate(ModuleUpdateForm moduleUpdateForm) {
        ModuleInventory moduleInventory;
        if (moduleUpdateForm.isStorageUpdate()) {
            var storage = storageRepository.getReferenceById(moduleUpdateForm.getStorageId());
            moduleInventory = storage.getModuleInventory();
            if (moduleUpdateForm.isStorageDisable()) {
                storage.setDisabled(true);
                moduleInventory.setAvailable(moduleInventory.getAvailable() - 1);
                logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("StorageDisable"), personService.getCurrentPerson(), storage.getPersistence());
            } else {
                storage.setProblematic(moduleUpdateForm.isProblematic());
                if (storage.isProblematic()) {
                    logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("StorageProblematic"), personService.getCurrentPerson(), storage.getPersistence());
                } else {
                    logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("StorageClear"), personService.getCurrentPerson(), storage.getPersistence());
                }
            }

        } else {
            moduleInventory = moduleInventoryRepository.getReferenceById(moduleUpdateForm.getModuleInventoryId());
            moduleInventory.setAvailable(moduleInventory.getAvailable() + Math.negateExact(moduleUpdateForm.getUpdatedValue()));
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("DecreaseInventory"), personService.getCurrentPerson(), moduleInventory.getPersistence());
        }
        moduleInventoryRepository.save(moduleInventory);
    }

    @Override
    public Optional<Device> getDevice(Long deviceId) {
        Optional<Device> currentDevice = deviceRepository.findById(deviceId);

        if (currentDevice.isPresent()) {
            if (currentDevice.get().getDevicePmCatalogList() != null) {
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

