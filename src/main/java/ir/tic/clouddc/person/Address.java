package ir.tic.clouddc.person;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Person")
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String value;

    public long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
