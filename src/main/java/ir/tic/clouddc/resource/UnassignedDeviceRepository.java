package ir.tic.clouddc.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnassignedDeviceRepository extends JpaRepository<UnassignedDevice, Integer> {

    boolean existsBySerialNumber(String serialNumber);

    @Query("select device.id as id," +
            " device.serialNumber as serialNumber," +
            " device.deviceCategory.category as category," +
            " device.deviceCategory.model as model," +
            " device.deviceCategory.vendor as vendor," +
            " device.deviceCategory.factor as factor," +
            " device.deviceCategory.factorSize as factorSize," +
            " device.deviceCategory.categoryId as categoryId" +
            " from UnassignedDevice device")
    List<ResourceService.DeviceIdSerialCategoryVendor_Projection1> getProjection2ForNewDeviceList();
}
