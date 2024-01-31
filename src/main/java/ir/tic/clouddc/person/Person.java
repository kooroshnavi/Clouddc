package ir.tic.clouddc.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Person")
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String name;

    @Column
    private boolean assignee; // false for manager and viewer

    @Column
    private char role;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Address address;

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

    @JsonIgnore
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
