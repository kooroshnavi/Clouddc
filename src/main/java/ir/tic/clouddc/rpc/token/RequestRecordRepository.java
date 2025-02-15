package ir.tic.clouddc.rpc.token;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRecordRepository extends CrudRepository<RequestRecord, Integer> {

}
