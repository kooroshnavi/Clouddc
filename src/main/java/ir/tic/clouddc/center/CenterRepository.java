package ir.tic.clouddc.center;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CenterRepository extends JpaRepository<Center, Integer> {
    @Query("SELECT C.id, C.name FROM Center C")
    List<CenterService.CenterIdNameProjection> fetchCenterIdNameList();

    @Query("select c from Center c where c.id in :centerIDList")
    List<Center> getCenterList(List<Integer> centerIDList);
}







