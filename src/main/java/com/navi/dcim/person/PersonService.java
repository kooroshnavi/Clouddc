package com.navi.dcim.person;

import java.util.List;

public interface PersonService {
    Person getPerson(int personId);
    Person getPerson(String name);

    List<Person> getPersonList();

    List getPersonListNotIn(int personId);

    Person addPerson(Person person);

    Person updatePerson(Person person);
}
