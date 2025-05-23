package ir.tic.clouddc.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("select d.id from Device d where d.serialNumber = :serialNumber")
    Optional<Long> getDeviceIdBySerialNumber(@Param("serialNumber") String serialNumber);

    boolean existsBySerialNumber(String serialNumber);

    @Query("select device.id as id, device.serialNumber as serialNumber, device.deviceCategory.category as category, device.deviceCategory.model as model" +
            " from Device device" +
            " where device.location.id = :locationId")
    List<ResourceService.DeviceIdSerialCategoryVendor_Projection1> getProjection2ForLocationDeviceList(@Param("locationId") Long locationId);

    @Query("select device.id as id," +
            " device.serialNumber as serialNumber," +
            " device.deviceCategory.category as category," +
            " device.deviceCategory.model as model," +
            " device.deviceCategory.vendor as vendor," +
            " device.deviceCategory.factor as factor," +
            " device.deviceCategory.factorSize as factorSize," +
            " device.deviceCategory.categoryId as categoryId" +
            " from Device device" +
            " where device.utilizer.id = :unassignedId")
    List<ResourceService.DeviceIdSerialCategoryVendor_Projection1> getProjection2ForNewDeviceList(@Param("unassignedId") Integer unassignedUtilizerId);


    @Query("select" +
            " device.id as deviceId," +
            " device.utilizer.id as deviceUtilizerId" +
            " from Device device" +
            " where device.location.id = :locationId")
    List<ResourceService.DeviceIdUtilizerId_Projection2> getProjection2List(@Param("locationId") Long locationId);

    @Query("select device from Device device where device.location.id = :locationId")
    List<Device> getLocationDeviceList(@Param("locationId") Long locationId);
}
