package ir.tic.clouddc.cloud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CloudRepository extends JpaRepository<CloudProvider, Integer> {

    @Query("select p.id as id, p.provider.name as name, p.serviceType as type from CloudProvider p")
    List<CloudService.ProviderIdNameProjection> getProviderList();
}
