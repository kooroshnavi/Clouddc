package ir.tic.clouddc.center;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationPmCatalogRepository extends JpaRepository<LocationPmCatalog, Short> {
}
