package ir.tic.clouddc.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PmRepository extends JpaRepository<Pm, Integer> {

    List<Pm> findByActive(boolean active);

    List<Pm> findByDuy(LocalDate date);


}
