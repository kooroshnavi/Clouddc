package ir.tic.clouddc.resource;

import ir.tic.clouddc.event.DeviceCheckList;
import ir.tic.clouddc.event.DeviceStatusForm;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ResourceService {

    List<DeviceIdSerialCategoryVendor_Projection1> getLocationDeviceListProjection(Long locationId);

    Utilizer getReferencedUtilizer(Integer utilizerId) throws EntityNotFoundException;

    List<DeviceIdSerialCategoryVendor_Projection1> getNewDeviceList();

    List<Device> getLocationDeviceList(Long locationId);

    List<DeviceCategory> getdeviceCategoryList();

    boolean checkResourceExistence(String serialNumber, int resourceType);

    void resourceRegister(ResourceRegisterForm resourceRegisterForm, int resourceType);

    UnassignedDevice getReferencedUnassignedDevice(Integer unassignedDeviceId);

    Supplier getReferencedDefaultSupplier();

    void deleteUnassignedDeviceIdList(List<Integer> unassignedDeviceIdList);

    boolean newDevicePresentCheck();

    String scheduleUnassignedDeviceRemoval();

    List<Utilizer> getUtilierList();

    Map<ModuleInventory, Integer> getModuleOverviewMap();

    List<ModuleInventory> getModuleCategoryList();

    List<ModuleInventory> getRelatedModuleInventoryList(Integer categoryId);

    List<Storage> getRelatedStorageList(Integer specId);

    Map<ModuleInventory, Integer> getDeviceModuleOverview(List<ModulePack> modulePackList);

    List<ModuleInventory> getDeviceCompatibleModuleInventoryList(Integer deviceCategoryID);

    long updateDeviceModule(ModuleUpdateForm moduleUpdateForm);

    List<Storage> getDeviceAssignedAndSpareStorageList(long deviceId, List<ModuleInventory> compatibleStorageInventoryList);

    void decreaseInventoryAvailability(ModuleUpdateForm moduleUpdateForm);

    interface DeviceIdSerialCategoryVendor_Projection1 {
        Long getId();

        String getSerialNumber();

        String getCategory();

        String getModel();

        String getVendor();

        String getFactor();

        Integer getFactorSize();

        Integer getCategoryId();
    }

    interface DeviceIdUtilizerId_Projection2 {
        Long getDeviceId();

        Integer getDeviceUtilizerId();
    }

    interface UtilizerIdNameProjection {
        Integer getId();

        String getName();
    }

    Optional<Device> getDevice(Long deviceId);

    Optional<Long> getDeviceIdBySerialNumber(String serialNumber);

    Device getReferencedDevice(Long deviceId) throws EntityNotFoundException;

    Utilizer getUtilizer(int utilizerId);

    void updateDeviceStatus(DeviceStatusForm deviceStatusForm, DeviceCheckList event);

    List<UtilizerIdNameProjection> getUtilizerListExcept(List<Integer> utilizerIdList);

    DeviceStatus getCurrentDeviceStatus(Device device);
}
