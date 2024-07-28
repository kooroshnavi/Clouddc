package ir.tic.clouddc.person;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Person")
@NoArgsConstructor
@Data
public final class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PersonID")
    private Integer id;

    @Column(name = "PhoneNumber")
    private String value;
}
