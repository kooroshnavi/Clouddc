package ir.tic.clouddc.center;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CenterRepository extends JpaRepository<Center, Short> {
    List<Center> findAllByIdIn(List<Long> ids);

    @Query("SELECT C.id AND C.name FROM Center C")
    List<CenterService.CenterIdNameProjection> fetchCenterIdNameList();



}







