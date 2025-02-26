package ir.tic.clouddc.otp;


import ir.tic.clouddc.person.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Person")
@NoArgsConstructor
@Getter
@Setter
public final class BackupCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BackCodeID")
    private Long id;

    @Column(name = "BackupCode", nullable = false)
    private String backupCode;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "PersonID", nullable = false)
    private Person person;
}
