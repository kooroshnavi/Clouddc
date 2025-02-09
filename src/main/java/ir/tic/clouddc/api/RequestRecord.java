package ir.tic.clouddc.api;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(schema = "API")
@NoArgsConstructor
@Getter
@Setter
public final class RequestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RequestRecordID")
    private int id;

    @ManyToOne
    @JoinColumn(name = "AuthToken")
    private AuthToken authToken;

    @Column(name = "RemoteAddress")
    private String remoteAddress;

    @Column(name = "RequestEstablished")
    private LocalDateTime localDateTime;
}
