package ir.tic.clouddc.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceCategoryRepository extends JpaRepository<DeviceCategory, Integer> {

    @Query("select distinct category from DeviceCategory category")
    List<DeviceCategory> getList();

}
