package com.navi.dcim.person;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Person")
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String value;


    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
