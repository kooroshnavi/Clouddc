package com.navi.dcim.center;

import com.navi.dcim.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;   // 10  - G10 Has Power and may contain a list of devices

    @Column
    private char row; // Row G

    @Column
    private int rack; // Rack 10

    @Column
    private boolean power; // Has power or not

    @OneToOne
    @JoinColumn(name = "center_id")
    private Center center;

    @OneToMany(mappedBy = "location")
    private List<Device> devices;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public char getRow() {
        return row;
    }

    public void setRow(char row) {
        this.row = row;
    }

    public int getRack() {
        return rack;
    }

    public void setRack(int rack) {
        this.rack = rack;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }


    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", row=" + row +
                ", rack=" + rack +
                ", power=" + power +
                ", center=" + center +
                ", devices=" + devices +
                '}';
    }
}
