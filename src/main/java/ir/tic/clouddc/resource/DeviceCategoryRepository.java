package ir.tic.clouddc.resource;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceCategoryRepository extends CrudRepository<DeviceCategory, Integer> {

    @Query("select distinct category from DeviceCategory category")
    List<DeviceCategory> getList();

}
