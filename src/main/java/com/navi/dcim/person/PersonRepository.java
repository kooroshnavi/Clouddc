package com.navi.dcim.person;

import com.navi.dcim.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    List<Person> findAllByIdNotIn(List personList);
    Person findByUsername(String name);
}
