package ir.tic.clouddc.cloud;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CephRepository extends CrudRepository<Ceph, Integer> {

    @Query("select c from Ceph c where c.current and c.provider.id = :providerId")
    Optional<Ceph> getCurrentCeph(@Param("providerId") int providerId);
}
