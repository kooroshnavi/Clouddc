package ir.tic.clouddc.person;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
final class PersonServiceImpl implements PersonService {

    private PersonRepository personRepository;

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
        return personRepository.findAllByAssignee(true);
    }

    @Override
    public List getPersonListNotIn(int personId) {
        List<Integer> ids = new ArrayList<>();
        ids.add(personId);
        return personRepository.findAllByIdNotInAndAssignee(ids, true);
    }

    @Override
    public Person addPerson(Person person) {
        return null;
    }

    @Override
    public Person updatePerson(Person person) {
        personRepository.save(person);
        return null;
    }

    @Override
    public int getPersonId(String username) {
        return personRepository.fetchPersonId(username);
    }

    @Override
    public Person getCurrentPerson() {
        return getPerson(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
