package ir.tic.clouddc.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtilizerRepository extends JpaRepository<Utilizer, Integer> {

    List<Utilizer> findAllByIdNotIn(List<Integer> utilizerList);

    @Query("select u.id as id, u.name as name from Utilizer u where u.id not in :idList")
    List<ResourceService.UtilizerIdNameProjection> getUtilizerProjectionExcept(List<Integer> idList);
}
