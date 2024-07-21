package ir.tic.clouddc.pm;

import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PmInterfaceRepository extends JpaRepository<PmInterface, Integer> {
    @Query("SELECT p FROM PmInterface p WHERE p.enabled = :enabled AND p.id NOT IN :pmInterfaceIdList")
    List<PmInterface> fetchPmInterfaceListNotInCatalogList(@Param("enabled") boolean enabled, List<Integer> pmInterfaceIdList);
}

