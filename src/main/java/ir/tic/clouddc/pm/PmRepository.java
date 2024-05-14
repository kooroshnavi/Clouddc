package ir.tic.clouddc.pm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PmRepository extends JpaRepository<Pm, Integer> {

    List<Pm> findByActive(boolean active);

    List<Pm> findBynextDue(LocalDate date);

    @Query("SELECT p FROM Pm p WHERE p.type.id = :typeId")
    List<Pm> fetchRelatedPmList(@Param("typeId") int typeId);


}
