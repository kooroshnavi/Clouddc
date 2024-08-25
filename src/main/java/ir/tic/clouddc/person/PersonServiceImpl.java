package ir.tic.clouddc.person;

import jakarta.persistence.EntityNotFoundException;
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


    @Autowired
    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Person getPersonByUsername(String name) {
        return personRepository.findByUsername(name);
    }

    @Override
    public List<Person> getPersonListExcept(List<String> ignoreUsernameList) {
        return personRepository.fetchAssignablePersonList(ignoreUsernameList, true);
    }

    @Override
    public Person getCurrentPerson() {
        return getPersonByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
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
    public List<Person> getDefaultAssgineeList() {
        return personRepository.findAllByAssignee(true);
    }

    @Override
    public Person getReferencedPerson(Integer personId) throws EntityNotFoundException {
        return personRepository.getReferenceById(personId);
    }

}
