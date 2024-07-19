package ir.tic.clouddc.individual;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    List<Person> findAllByAssignee(boolean assignee);

    List<Person> findAllByUsernameNotInAndAssignee(List<String> usernameList, boolean assignee);

    Person findByUsername(String name);

    @Query("SELECT id FROM Person p WHERE p.username  = :username")
    int fetchPersonId(@Param("username") String username);


}
