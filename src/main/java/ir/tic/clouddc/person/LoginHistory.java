package ir.tic.clouddc.person;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(schema = "Person")
@NoArgsConstructor
@Getter
@Setter
public final class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LoginHistoryID")
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "PersonID")
    private Person person;

    @Column(name = "Client")
    private String remoteAddress;

    @Column(name = "Date")
    private LocalDate localDate;

    @Column(name = "Time")
    private LocalTime localTime;

    @Column(name = "Successful")
    private boolean successful;

    @Transient
    private String persianLoginDate;

    public LoginHistory(Person person, String remoteAddress, LocalDate localDate, LocalTime localTime, boolean isSuccessful) {
        this.person = person;
        this.remoteAddress = remoteAddress;
        this.localDate = localDate;
        this.localTime = localTime;
        this.successful = isSuccessful;
    }
}
