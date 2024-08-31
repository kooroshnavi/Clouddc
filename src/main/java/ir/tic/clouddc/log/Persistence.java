package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(schema = "Log")
@NoArgsConstructor
@Getter
@Setter
public final class Persistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PersistenceID")
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "PersonID")
    private Person person;

    @OneToMany(mappedBy = "persistence", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    private List<LogHistory> logHistoryList;

    public Persistence(LocalDate date, LocalTime time, Person owner, String logMessageKey) {
        this.setPerson(owner);
        LogHistory logHistory = new LogHistory(date, time, owner, this, UtilService.LOG_MESSAGE.get(logMessageKey), true);
        this.setLogHistoryList(List.of(logHistory));
    }

    public Persistence(Person person) {
        this.person = person;
    }
}