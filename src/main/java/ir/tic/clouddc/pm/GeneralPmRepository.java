package ir.tic.clouddc.pm;

import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralPmRepository extends JpaRepository<GeneralPm, Integer> {

    @Query("SELECT G FROM GeneralPm G WHERE t.task.id = :taskId")
    List<Long> fetchGeneralPmList(@Param("pmInterfaceId") short pmInterfaceId, @Param("active") boolean active,
                                  @Param("locationName") @Nullable String locationName,
                                  @Param("username") @Nullable String username);

}
