package ir.tic.clouddc.pm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PmInterfaceRepository extends JpaRepository<PmInterface, Short> {
    @Query("SELECT p.id FROM PmInterface p WHERE p.type.id = :typeId")
    List<Pm> fetchRelatedPmList(@Param("typeId") int typeId);

}
