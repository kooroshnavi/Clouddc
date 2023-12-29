package com.navi.dcim.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String name;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column
    private String address;

    @Column
    private boolean assignee; // false for managers

    @JsonIgnore
    public boolean isAssignee() {
        return assignee;
    }

    @JsonIgnore
    public String getAddress() {
        return address;
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
