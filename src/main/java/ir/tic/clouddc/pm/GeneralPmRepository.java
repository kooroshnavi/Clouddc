package ir.tic.clouddc.pm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralPmRepository extends JpaRepository<GeneralLocationPm, Integer> {

}
