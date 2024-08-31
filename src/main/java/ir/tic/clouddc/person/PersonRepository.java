package ir.tic.clouddc.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    List<Person> findAllByAssignee(boolean assignee);

    @Query("SELECT p from Person p where p.username not in :usernameList and p.assignee = :assignee")
    List<Person> fetchAssignablePersonList(List<String> usernameList, @Param("assignee") boolean assignee);

    Person findByUsername(String name);

}
