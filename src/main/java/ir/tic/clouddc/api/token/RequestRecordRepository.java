package ir.tic.clouddc.api.token;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRecordRepository extends CrudRepository<RequestRecord, Integer> {

}
