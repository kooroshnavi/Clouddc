package com.navi.dcim.person;

import com.navi.dcim.model.Person;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PersonService {
    Person getPerson(int personId);
    Person getPerson(String name);

    List<Person> getPersonList();

    List getPersonListNotIn(int personId);

    Person addPerson(Person person);

    Person updatePerson(Person person);
}
