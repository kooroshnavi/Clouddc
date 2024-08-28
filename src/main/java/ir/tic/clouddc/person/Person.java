package ir.tic.clouddc.person;

import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Person")
@NoArgsConstructor
@Getter
@Setter
public final class Person {

    @Id
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

    @OneToMany(mappedBy = "person")
    private List<Persistence> personList;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @MapsId
    @JoinColumn(name = "AddressID")
    private Address address;

    @Transient
    private long workspaceSize;
}
