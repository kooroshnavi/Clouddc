package ir.tic.clouddc.document;

import ir.tic.clouddc.log.Persistence;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MetaDataRepository extends JpaRepository<MetaData, Long> {

    @Query("SELECT m FROM MetaData m WHERE m.persistence.id IN :persistenceIdList AND m.enabled = :enable")
    List<MetaData> fetchRelatedMetaDataList(List<Long> persistenceIdList, @Nullable @Param("enable") Boolean enable);

    @Query("SELECT m.persistence.person.id FROM MetaData m WHERE m.id  = :id")
    int fetchMetaDataOwnerId(@Param("id") Long id);

    @Query("SELECT m.persistence FROM MetaData m WHERE m.id  = :id")
    Persistence fetchMetaDataPersistence(@Param("id") Long metadataId);

    @Transactional
    @Modifying
    @Query("update MetaData m set m.enabled = :enabled, m.removeDate = :removeDate where m.id = :metadataId")
    void disableMetadata(@Param("metadataId") Long id, @Param("enabled") boolean enabled, @Param("removeDate") LocalDate date);

    @Query("SELECT m FROM MetaData m WHERE m.persistence.id IN :persistenceIdList")
    List<MetaData> fetchFullMetadataList(List<Long> persistenceIdList);

    @Query("SELECT m.id FROM MetaData m WHERE m.enabled = :enabled and m.removeDate = :date")
    List<Long> getRemovalDueIdList(@Param("date") LocalDate date, @Param("enabled") boolean enabled);
}
