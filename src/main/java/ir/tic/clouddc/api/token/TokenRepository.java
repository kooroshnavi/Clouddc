package ir.tic.clouddc.api.token;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<AuthenticationToken, Integer> {

    @Query("select t.id as id, t.token as token from AuthenticationToken t where t.valid and t.expiryDate >= current date ")
    List<TokenService.ValidIdTokenProjection> getValidTokens();

    @Query("select t from AuthenticationToken t where t.person.username = :username")
    List<AuthenticationToken> getPersonTokenList(@Param("username") String username);

    boolean existsByValidAndPersonUsernameAndExpiryDateIsGreaterThanEqual(boolean valid, String username, LocalDate localDate);

    @Transactional
    @Modifying
    @Query("update AuthenticationToken t set t.valid = :valid, t.expiryDate = :expiryDate where t.person.username = :username and t.valid")
    void revokeToken(@Param("username") String username, @Param("valid") boolean valid, @Param("expiryDate") LocalDate expiryDate);
}
