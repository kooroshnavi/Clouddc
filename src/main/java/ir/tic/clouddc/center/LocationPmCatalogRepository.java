package ir.tic.clouddc.center;

import ir.tic.clouddc.pm.PmInterface;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationPmCatalogRepository extends JpaRepository<LocationPmCatalog, LocationPmCatalog> {

    List<LocationPmCatalog> findAllByNextDueDateAndEnabled(@Nullable LocalDate date, boolean enabled);

    Optional<LocationPmCatalog> findByPmInterfaceAndLocation(@Nullable PmInterface pmInterface, @Nullable Location location);

    List<LocationPmCatalog> findAllByPmInterface(PmInterface pmInterface);

    List<LocationPmCatalog> findAllByLocation(Location location);

    @Transactional
    @Modifying
    @Query("update LocationPmCatalog l set l.nextDueDate = :nextDue where l.id = :id")
    void updateCatalogDueDate(@Param("nextDue") LocalDate date, @Param("id") Long id);

}
