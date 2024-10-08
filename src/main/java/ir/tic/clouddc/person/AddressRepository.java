package ir.tic.clouddc.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    boolean existsByValue(String phoneNumber);
    Optional<Address> findByValue(String value);
}
