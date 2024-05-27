package ir.tic.clouddc.center;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataCenterRepository extends JpaRepository<Center, Integer> {

    @Query("SELECT D.name FROM DataCenter D")
    List<String> fetchAllIdNameMap();
}
