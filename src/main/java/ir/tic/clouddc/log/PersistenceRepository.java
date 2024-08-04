package ir.tic.clouddc.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersistenceRepository extends JpaRepository<Persistence, Long> {

    @Query("select p.id from Persistence p where p.person.id = :personId")
    List<Long> getPersonPersistenceIdList(@Param("personId") Integer personId);
}
