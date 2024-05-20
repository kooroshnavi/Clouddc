package ir.tic.clouddc.document;

import ir.tic.clouddc.log.Persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaDataRepository extends JpaRepository<MetaData, Long> {

    @Query("SELECT m FROM MetaData m WHERE m.persistence.id IN :persistenceIdList")
    List<MetaData> fetchRelatedMetaDataList(List<Long> persistenceIdList);

    @Query("SELECT (m.persistence.person.id) FROM MetaData m WHERE m.id  = :id")
    int fetchMetaDataOwnerName(@Param("id") long id);

    @Query("SELECT (m.persistence) FROM MetaData m WHERE m.id  = :id")
    Persistence fetchMetaDataPersistence(@Param("id") long id);

}
