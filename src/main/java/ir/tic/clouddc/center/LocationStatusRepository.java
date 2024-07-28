package ir.tic.clouddc.center;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationStatusRepository extends JpaRepository<LocationStatus, Integer> {

    Optional<LocationStatus> findByLocationAndActive(Location location, boolean current);
}
