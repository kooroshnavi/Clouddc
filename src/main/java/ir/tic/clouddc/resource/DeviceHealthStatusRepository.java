package ir.tic.clouddc.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceHealthStatusRepository extends JpaRepository<DeviceStatus, Integer> {
    Optional<DeviceStatus> findByCurrent(boolean current);
}
