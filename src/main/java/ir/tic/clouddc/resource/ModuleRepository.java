package ir.tic.clouddc.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    @Query("SELECT m.moduleCategory FROM Module m WHERE m.spare = :spare and m.localityId in :localityIdList")
    List<ModuleCategory> getLocalityCategoryList(List<Long> localityIdList, @Param("spare") boolean spare);

    boolean existsBySerialNumber(String serialNumber);
}
