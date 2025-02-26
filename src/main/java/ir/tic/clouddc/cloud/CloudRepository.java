package ir.tic.clouddc.cloud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CloudRepository extends JpaRepository<CloudProvider, Integer> {

    @Query("select p from CloudProvider p where p.current and p.serviceType = :serviceType and p.provider.id = :providerID")
    Optional<CloudProvider> getCurrentCloudProvider(@Param("serviceType") char serviceType, @Param("providerID") int providerID);


    @Query("select p.id as id, p.localDateTime as date from CloudProvider p where p.serviceType = :serviceType and p.provider.id = :providerID")
    List<CloudService.CloudProviderIDLocalDateProjection> getServiceHistory(@Param("serviceType") char serviceType, @Param("providerID") int providerID);

    @Query("select p from CloudProvider p where p.id = :serviceId")
    Optional<CloudProvider> getCloudProvider(@Param("serviceId") int serviceId);


}
