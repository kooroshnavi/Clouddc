package ir.tic.clouddc.person;

import ir.tic.clouddc.center.LocationPmCatalog;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Person")
@NoArgsConstructor
@Data
public final class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PersonID")
    private Integer id;

    @Column(name = "Username")
    private String username;

    @Column(name = "FullName")
    private String name;

    @Column(name = "Assignable")
    private boolean assignee; // false for manager and viewer

    @Column(name = "RoleCode")
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
