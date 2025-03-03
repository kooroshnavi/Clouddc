package ir.tic.clouddc.otp;

import ir.tic.clouddc.person.Person;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackupCodeRepository extends JpaRepository<BackupCode, Long> {

    interface BackupCodeProjection {
        String getCode();
    }

    @Query("select b.backupCode as code from BackupCode b where b.person = :currentPerson")
    List<BackupCodeProjection> getBackupCodeList(@Param("currentPerson") Person currentPerson);

    boolean existsByPerson_Address_ValueAndBackupCode(String address, String backupCode);

    @Transactional
    @Modifying
    void deleteByPerson_Address_ValueAndBackupCode(String address, String backupCode);

    @Query("select b from BackupCode b where b.person = :currentPerson")
    List<BackupCode> getPersonBackupCodeList(@Param("currentPerson") Person currentPerson);
}
