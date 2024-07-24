package ir.tic.clouddc.pm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PmInterfaceCatalogRepository extends JpaRepository<PmInterfaceCatalog, Long> {

    @Query("select c from PmInterfaceCatalog c where c.pmInterface.enabled = :interfaceEnabled and c.enabled = :catalogEnabled and c.nextDueDate = :dueDate")
    List<PmInterfaceCatalog> getTodayCatalogList(@Param("dueDate") LocalDate date
            , @Param("interfaceEnabled") boolean interfaceEnabled
            , @Param("catalogEnabled") boolean catalogEnabled);
}
