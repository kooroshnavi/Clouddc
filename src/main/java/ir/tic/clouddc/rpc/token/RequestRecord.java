package ir.tic.clouddc.rpc.token;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "RequestRecord", schema = "API")
@NoArgsConstructor
@Getter
@Setter
public class RequestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RequestRecordID")
    private int id;

    @ManyToOne
    @JoinColumn(name = "AuthenticationToken", nullable = false)
    private AuthenticationToken authenticationToken;

    @Column(name = "RemoteAddress", nullable = false)
    private String remoteAddress;

    @Column(name = "CaptureDateTime", nullable = false)
    private LocalDateTime localDateTime;

    @Column(name = "wasSuccessful", nullable = false)
    private boolean successful;

    @Transient
    private String persianDateTime;

}
