package ir.tic.clouddc.center;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalonPmDueRepository extends JpaRepository<SalonPmDue, Integer> {
    List<SalonPmDue> findByDue(LocalDate date);
}
