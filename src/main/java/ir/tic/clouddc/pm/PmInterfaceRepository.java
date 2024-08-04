package ir.tic.clouddc.pm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PmInterfaceRepository extends JpaRepository<PmInterface, Integer> {
    @Query("select p from PmInterface p where p.enabled = :enabled and p.id not in :pmInterfaceIdList and p.target in :targetIdList")
    List<PmInterface> fetchPmInterfaceListNotInCatalogList(@Param("enabled") boolean enabled
                    , List<Integer> pmInterfaceIdList, List<Integer> targetIdList);
}

