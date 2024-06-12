package ir.tic.clouddc.center;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallRepository extends JpaRepository<Hall, Integer> {

    @Query("SELECT H.id AND H.name FROM Hall L")
    List<CenterService.HallIdName> fetchHallIdNameList();
}
