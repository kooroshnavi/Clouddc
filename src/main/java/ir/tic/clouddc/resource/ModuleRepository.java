package ir.tic.clouddc.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    @Query("select m from Module m where m.spare = :spare and m.localityId = :deviceId")
    List<Module> getDeviceModuleList(@Param("deviceId") long deviceId, @Param("spare") boolean spare);

    @Query("SELECT m.moduleCategory FROM Module m WHERE m.spare = :spare and m.localityId = :deviceId")
    List<ModuleCategory> getDeviceModuleCategoryList(@Param("deviceId") Long deviceId, @Param("spare") boolean spare);
}
