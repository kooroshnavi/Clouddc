package ir.tic.clouddc.center;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    @Query("SELECT L.name FROM Location L WHERE L.type = :type")
    List<String> getNameList(@Param("type") String type);

    List<Rack> findAllByRack();
}
