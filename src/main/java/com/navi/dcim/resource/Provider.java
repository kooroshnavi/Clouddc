package com.navi.dcim.resource;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String name;

    @OneToMany(mappedBy = "provider")
    private List<DeviceType> deviceTypeList;

    @OneToMany(mappedBy = "provider")
    private List<ModuleType> moduleTypeList;
}
