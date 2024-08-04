package ir.tic.clouddc.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtilizerRepository extends JpaRepository<Utilizer, Integer> {

    List<Utilizer> findAllByIdNotIn(List<Integer> utilizerList);

}
