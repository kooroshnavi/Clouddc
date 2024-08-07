package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationStatusEventRepository extends JpaRepository<LocationCheckList, Long> {
    List<LocationCheckList> findAllByLocation(Location location);
}
