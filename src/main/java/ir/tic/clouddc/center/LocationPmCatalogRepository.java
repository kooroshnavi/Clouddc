package ir.tic.clouddc.center;

import ir.tic.clouddc.pm.PmInterface;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationPmCatalogRepository extends JpaRepository<LocationPmCatalog, Short> {

    List<LocationPmCatalog> findAllByNextDueDateAndEnabled(@Nullable LocalDate date, boolean enabled);

    Optional<LocationPmCatalog> findByPmInterfaceAndLocation(@Nullable PmInterface pmInterface, @Nullable Location location);

    List<LocationPmCatalog> findAllByPmInterface(PmInterface pmInterface);

    List<LocationPmCatalog> findAllByLocation(Location location);
}
