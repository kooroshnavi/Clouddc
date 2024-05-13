package ir.tic.clouddc.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaDataRepository extends JpaRepository<MetaData, Long> {

    @Query("SELECT m FROM MetaData m WHERE m.persistence.id IN :persistenceIdList")
    List<MetaData> fetchRelatedMetaDataList(List<Long> persistenceIdList);

}
