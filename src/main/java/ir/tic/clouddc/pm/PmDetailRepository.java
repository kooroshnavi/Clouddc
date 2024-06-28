package ir.tic.clouddc.pm;


import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PmDetailRepository extends JpaRepository<PmDetail, Integer> {
    Optional<PmDetail> findByPmIdAndActive(int pmId, boolean active);
    List<GeneralPmDetail> findByTaskId(long taskId);
    @Query("SELECT p.pm FROM PmDetail p WHERE p.persistence.person.username = :username AND p.active = :active")
    List<Pm> fetchPmListByActivationAndPerson(@Param("username") @Nullable String username, @Param("active") boolean active);

    @Query("SELECT p.persistence.id FROM PmDetail p WHERE p.pm.id = :pmId")
    List<Long> getPersistenceIdList(@Param("pmId") int pmId);

    @Query("SELECT p.persistence.person.username FROM PmDetail p WHERE p.pm.id = :pmId AND p.active = :active")
    String fetchPmOwnerUsername(@Param("pmId") int pmId, @Param("active") boolean active);

}
