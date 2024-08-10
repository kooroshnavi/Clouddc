package ir.tic.clouddc.center;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT L FROM Location L WHERE L.locationCategory.category IN :locationCategoryNameList")
    List<Location> fetchCustomizedLocationList(List<String> locationCategoryNameList);

    @Query("select loc from Location loc where loc.assignable and loc.id not in :locationId")
    List<Location> getLocationListNotIn(List<Long> locationId);
}
