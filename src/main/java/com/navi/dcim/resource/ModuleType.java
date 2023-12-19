package com.navi.dcim.resource;


import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class ModuleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id; // 1 2 3 ...
    @Column
    private float capacity; //1. 14,20,3.84... 2. 32,64,... 3. 1,10,40,100,...
    @Column
    private String category; // 1. Storage 2. Memory 3. Transceiver
    @Column
    private String type; // 1. Hdd, ssd   2. RAM    3. SFP, QSFP, CVR
    @Column
    private String model; // 1. Sas, sas   3. SR, 28, LR4,...
    @Column
    private String partNumber;
    @OneToMany(mappedBy = "moduleType")
    private List<Module> moduleList;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider; // hp, seagate, cisco,...


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getCapacity() {
        return capacity;
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public List<Module> getModuleList() {
        return moduleList;
    }

    public void setModuleList(List<Module> moduleList) {
        this.moduleList = moduleList;
    }



    @Override
    public String toString() {
        return "ModuleType{" +
                "id=" + id +
                ", capacity=" + capacity +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", model='" + model + '\'' +
                ", provider='" + provider + '\'' +
                ", partNumber='" + partNumber + '\'' +
                ", moduleList=" + moduleList +
                '}';
    }
}
