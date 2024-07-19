package ir.tic.clouddc.log;

import ir.tic.clouddc.individual.Person;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "log")
@NoArgsConstructor
public class Persistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Person person;

    @OneToMany(mappedBy = "persistence")
    private List<LogHistory> logHistoryList;

    public Persistence(Person person) {
        this.person = person;
    }

    public long getId() {
        return id;
    }

    public List<LogHistory> getLogHistoryList() {
        return logHistoryList;
    }

    public void setLogHistoryList(List<LogHistory> logHistoryList) {
        this.logHistoryList = logHistoryList;
    }

    public Person getPerson() {
        return person;
    }
}