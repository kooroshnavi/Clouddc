package ir.tic.clouddc.rpc.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.rpc.response.Result;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "AuthenticationToken", schema = "API")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class AuthenticationToken extends Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TokenID")
    @JsonIgnore
    private int id;

    @Column(name = "Token", unique = true, nullable = false)
    private String token;

    @Column(name = "CreatedAt", nullable = false)
    @JsonIgnore
    private LocalDateTime registerDate;

    @Column(name = "ExpiryDate", nullable = false)
    @JsonIgnore
    private LocalDate expiryDate;

    @Column(name = "Validity", nullable = false)
    private boolean valid;

    @OneToMany(mappedBy = "authenticationToken", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<RequestRecord> requestRecords;

    @ManyToOne
    @JoinColumn(name = "Owner")
    private Person person;

    @Transient
    private String persianRegisterDate;

    @Transient
    private String persianExpiryDate;
}
