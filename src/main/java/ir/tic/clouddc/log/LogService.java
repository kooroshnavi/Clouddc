package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;

import java.time.LocalDate;
import java.time.LocalTime;

public interface LogService {

    Persistence persistenceSetup(Person person);

    void historyUpdate(LocalDate date, LocalTime time, char actionCode, Person person, Persistence persistence);

}
