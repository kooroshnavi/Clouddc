package ir.tic.clouddc.pm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PmRepository extends JpaRepository<Pm, Long> {

    List<Pm> findAllByActive(boolean active);

    @Query("select p from Pm p where p.catalog.pmInterface.id = :id and p.active = :active")
    List<Pm> fetchActivePmList(@Param("id") Integer pmInterfaceId, @Param("active") boolean active);
}
