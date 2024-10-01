package ir.tic.clouddc.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

    List<Person> findAllByAssigneeAndEnabled(boolean assignee, boolean enabled);

    @Query("SELECT p from Person p where p.enabled and p.username not in :usernameList and p.assignee = :assignee")
    List<Person> fetchAssignablePersonList(List<String> usernameList, @Param("assignee") boolean assignee);

    @Query("SELECT p from Person p where p.username = :name")
    Person fetchByUsername(String name);

    @Query("select person.id as id," +
            " person.name as name," +
            " person.assignee as assignee," +
            " person.enabled as enabled," +
            " person.role as role," +
            " person.workSpaceSize as workspaceSize" +
            " from Person person")
    List<PersonService.PersonProjection_1> getPersonProjection_1();

    @Query("select p from Person p where p.address.value = :phoneNumber")
    Person fetchByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
