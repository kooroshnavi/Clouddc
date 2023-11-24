package com.navi.dcim.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
final class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }


    @Override
    public Person getPerson(int personId) {
        return personRepository.findById(personId).get();
    }

    @Override
    public Person getPerson(String name) {
        return personRepository.findByUsername(name);
    }

    @Override
    public List<Person> getPersonList() {
        return personRepository.findAll();
    }

    @Override
    public List getPersonListNotIn(int personId) {
        List<Integer> ids = new ArrayList<>();
        ids.add(personId);
        return personRepository.findAllByIdNotIn(ids);
    }

    @Override
    public Person addPerson(Person person) {
        return null;
    }

    @Override
    public Person updatePerson(Person person) {
        return null;
    }
}
