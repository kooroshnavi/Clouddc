package ir.tic.clouddc.person;

import ir.tic.clouddc.resource.Utilizer;
import ir.tic.clouddc.resource.UtilizerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
final class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    private final UtilizerRepository utilizerRepository;


    @Autowired
    public PersonServiceImpl(PersonRepository personRepository, UtilizerRepository utilizerRepository) {
        this.personRepository = personRepository;
        this.utilizerRepository = utilizerRepository;
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
    public List<Person> getPersonListExcept(List<String> ignoreUsernameList) {
        return personRepository.findAllByUsernameNotInAndAssignee(ignoreUsernameList, true);
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

    @Override
    public List<GrantedAuthority> getCurrentPersonRoleList() {
        return new ArrayList<>(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }

    @Override
    public List<Utilizer> getUtilizerList() {
        return utilizerRepository.findAll();
    }

    @Override
    public List<Person> getDefaultAssgineeList() {
        return personRepository.findAllByAssignee(true);
    }

}
