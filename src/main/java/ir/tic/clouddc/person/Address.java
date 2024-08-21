package ir.tic.clouddc.person;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Person")
@NoArgsConstructor
@Getter
@Setter
public final class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PersonID")
    private Integer id;

    @Column(name = "PhoneNumber", unique = true, nullable = false)
    private String value;
}
