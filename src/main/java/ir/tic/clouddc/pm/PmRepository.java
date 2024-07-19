package ir.tic.clouddc.pm;

import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PmRepository extends JpaRepository<Pm, Integer> {

    List<Pm> findAllByActive(boolean active);

    List<Pm> findAllByPmInterfaceAndActiveAndLocationId(PmInterface pmInterface, boolean active, @Nullable Integer locationId);
}
