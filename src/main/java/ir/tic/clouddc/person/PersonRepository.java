package ir.tic.clouddc.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    List<Person> findAllByAssignee(boolean assignee);
    List<Person> findAllByIdNotInAndAssignee(List personList, boolean assignee);
    Person findByUsername(String name);


}
