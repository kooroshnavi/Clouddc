package ir.tic.clouddc.individual;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "individual")
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

    public void setValue(String value) {
        this.value = value;
    }

}
