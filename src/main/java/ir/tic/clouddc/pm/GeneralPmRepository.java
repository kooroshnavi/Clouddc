package ir.tic.clouddc.pm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralPmRepository extends JpaRepository<GeneralPm, Integer> {
    List<GeneralPm> findAllByIdAndActive(int id, boolean active);
}
