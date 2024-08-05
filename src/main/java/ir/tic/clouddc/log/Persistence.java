package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Log")
@NoArgsConstructor
@Data
public final class Persistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PersistenceID")
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "PersonID")
    private Person person;

    @OneToMany(mappedBy = "persistence", cascade = CascadeType.ALL)
    private List<LogHistory> logHistoryList;

    public Persistence(Long id) {
        this.id = id;
    }

    public Persistence(Person person) {
        this.person = person;
    }

}