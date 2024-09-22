package ir.tic.clouddc.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    boolean existsBySerialNumber(String serialNumber);

    @Query("select s.id from Storage s where s.localityId = :localityId")
    List<Long> getDeviceStorageIdList(@Param("localityId") long localityId);

    @Query("select s from Storage s where s.moduleInventory.id in :storageSpecificationIdList")
    List<Storage> getRelatedStorageList(List<Integer> storageSpecificationIdList);

    @Query("select s from Storage s where (s.spare and s.moduleInventory in :compatibleStorageInventoryList) or s.localityId = :deviceId")
    List<Storage> fetchDeviceAssignedAndSpareList(@Param("deviceId") long deviceId, List<ModuleInventory> compatibleStorageInventoryList);
}
