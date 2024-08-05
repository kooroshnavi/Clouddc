package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Location;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findBySerialNumber(String serialNumber);

    @Query("select device.id as id, device.serialNumber as serialNumber, device.deviceCategory.category as category, device.deviceCategory.model as model" +
            " from Device device" +
            " where device.location.id = :locationId")
    List<ResourceService.DeviceIdSerialCategoryProjection> getDeviceProjection(@Param("locationId") Long locationId);

    @Transactional
    @Modifying
    @Query("update Device d set d.location = :location, d.utilizer = :utilizer where d in :deviceList")
    void updateDeviceLocationAndUtilizer(List<Device> deviceList, @Param("location") Location destinationLocation, @Param("utilizer") Utilizer destinationUtilizer);
}
