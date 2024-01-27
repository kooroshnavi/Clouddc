package ir.tic.clouddc.center;


import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalonChecklistRepository {
    List<SalonChecklist> findByDue(LocalDate date);
}
