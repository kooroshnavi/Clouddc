package ir.tic.clouddc.individual;

import ir.tic.clouddc.center.LocationPmCatalog;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(schema = "individual")
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

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


    public List<LocationPmCatalog> getLocationPmCatalogList() {
        return locationPmCatalogList;
    }

    public void setLocationPmCatalogList(List<LocationPmCatalog> locationPmCatalogList) {
        this.locationPmCatalogList = locationPmCatalogList;
    }

    public Person(int id) {
        this.id = id;
    }

    public char getRole() {
        return role;
    }

    public void setRole(char role) {
        this.role = role;
    }

    public void setAssignee(boolean assignee) {
        this.assignee = assignee;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public boolean isAssignee() {
        return assignee;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }
}
