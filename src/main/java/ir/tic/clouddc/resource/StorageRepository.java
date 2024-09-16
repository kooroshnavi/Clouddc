package ir.tic.clouddc.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    boolean existsBySerialNumber(String serialNumber);

    @Query("select s from Storage s where s.moduleInventory.id in :storageSpecificationIdList")
    List<Storage> getRelatedStorageList(List<Integer> storageSpecificationIdList);
}
