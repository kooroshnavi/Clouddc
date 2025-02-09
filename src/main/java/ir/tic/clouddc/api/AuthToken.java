package ir.tic.clouddc.api;

import ir.tic.clouddc.person.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(schema = "API")
@NoArgsConstructor
@Getter
@Setter
public final class AuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TokenID")
    private int id;

    @Column(name = "Token")
    private String token;

    @Column(name = "CreatedAt")
    private LocalDateTime createdDate;

    @Column(name = "ExpiryDate")
    private LocalDateTime expiryDate;

    @Column(name = "Validity")
    private boolean valid;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "PersonID")
    private Person person;

    @OneToMany(mappedBy = "authToken")
    private List<RequestRecord> requestRecordList;

}
