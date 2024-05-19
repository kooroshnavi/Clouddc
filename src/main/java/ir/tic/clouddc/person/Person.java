package ir.tic.clouddc.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.tic.clouddc.log.LogHistory;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(schema = "Person")
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

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column
    private boolean assignee; // false for manager and viewer

    @Column
    private char role;

    @OneToMany(mappedBy = "person" , cascade = {CascadeType.ALL})
    private List<LogHistory> logHistoryList;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Address address;


    public Person(int id) {
        this.id = id;
    }

    public List<LogHistory> getLogHistoryList() {
        return logHistoryList;
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

    @JsonIgnore
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    @JsonIgnore
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
