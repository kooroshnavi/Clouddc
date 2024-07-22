package ir.tic.clouddc.log;

import ir.tic.clouddc.individual.Person;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "log")
@NoArgsConstructor
@Data
public class Persistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Person person;

    @OneToMany(mappedBy = "persistence")
    private List<LogHistory> logHistoryList;

    public Persistence(Long id) {
        this.id = id;
    }

    public Persistence(Person person) {
        this.person = person;
    }
}