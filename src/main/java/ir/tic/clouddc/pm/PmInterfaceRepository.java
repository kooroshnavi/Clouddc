package ir.tic.clouddc.pm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PmInterfaceRepository extends JpaRepository<PmInterface, Integer> {

    @Query("select p from PmInterface p WHERE p.enabled = :enabled and p not in :pmCatalogList")
    List<PmInterface> fetchPmInterfaceListNotInCatalogList(List<PmInterface> pmCatalogList, @Param("enabled") boolean enabled);
}
