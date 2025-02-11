package ir.tic.clouddc.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Integer> {

    boolean existsByTokenAndValidAndExpiryDateAfter(String token, boolean valid, LocalDateTime localDateTime);
}
