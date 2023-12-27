package com.navi.dcim.center;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

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


    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", row=" + row +
                ", rack=" + rack +
                ", power=" + power +
                ", center=" + center +
                '}';
    }
}
