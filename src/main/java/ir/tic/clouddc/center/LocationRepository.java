package ir.tic.clouddc.center;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    @Query("SELECT L FROM Location L WHERE L.locationCategory.target IN :locationCategoryNameList")
    List<Location> fetchCustomizedLocationList(List<String> locationCategoryNameList);
}
