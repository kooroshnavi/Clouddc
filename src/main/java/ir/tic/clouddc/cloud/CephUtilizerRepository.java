package ir.tic.clouddc.cloud;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CephUtilizerRepository extends CrudRepository<CephUtilizer, Integer> {

    interface CephUtilizerProjection {
        float getUsage();

        String getUnit();

        int getUtilizerId();

        String getUtilizerName();
    }

    @Query("select" +
            " c.usage as usage," +
            " c.unit as unit," +
            " c.utilizer.id as utilizerId," +
            " c.utilizer.name as utilizerName" +
            " from CephUtilizer c" +
            " where c.provider.current and c.provider.provider.id = :providerId")
    List<CephUtilizerProjection> getCephUtilizerList(@Param("providerId") int providerId);

}
