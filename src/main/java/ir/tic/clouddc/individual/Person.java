package ir.tic.clouddc.individual;

import ir.tic.clouddc.center.LocationPmCatalog;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(schema = "individual")
@NoArgsConstructor
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private String username;

    @Column
    @Nationalized
    private String name;

    @Column
    private boolean assignee; // false for manager and viewer

    @Column
    private char role;

    @OneToMany(mappedBy = "defaultPerson")
    private List<LocationPmCatalog> locationPmCatalogList;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Address address;

    public Person(Integer id) {
        this.id = id;
    }
}
