package ir.tic.clouddc.person;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
final class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository, AddressRepository addressRepository) {
        this.personRepository = personRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public Person getPerson(long personId) {

        return personRepository.findById(personId).get();
    }

    @Override
    public List<Person> getPersonList() {
        return personRepository.findAllByAssignee(true);
    }

    @Override
    public List<Person> getPersonListNotIn(long personId) {
        List<Long> ids = new ArrayList<>();
        ids.add(personId);
        return personRepository.findAllByIdNotInAndAssignee(ids, true);
    }

    @Override
    public Person addPerson(Person person) {
        return null;
    }

    @Override
    public long getAuthenticatedPersonId() {
        return Long
                .parseLong(SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal()
                        .toString());
    }

    @Override
    public Person updatePerson(Person person) {
        personRepository.save(person);
        return null;
    }

    @Override
    public Optional<Address> getPersonAddress(long personId) {
        return addressRepository.findById(personId);
    }

}
