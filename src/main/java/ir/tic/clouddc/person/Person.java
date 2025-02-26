package ir.tic.clouddc.person;

import ir.tic.clouddc.api.token.AuthenticationToken;
import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.otp.BackupCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Person")
@NoArgsConstructor
@Getter
@Setter
public final class Person {

    @Id
    private Integer id; // 1

    @Column(name = "Username", unique = true)
    private String username;

    @Column(name = "FullName")
    private String name; // 2

    @Column(name = "Enabled") // 4
    private boolean enabled;

    @Column(name = "Assignable", nullable = false)
    private boolean assignee; // false for manager and viewer // 3

    @Column(name = "RoleCode", nullable = false)  // 5
    private char role;

    @Column(name = "WorkspaceSize")
    private Integer workspaceSize;

    @OneToMany(mappedBy = "defaultPerson")
    private List<LocationPmCatalog> locationPmCatalogList;

    @OneToMany(mappedBy = "person", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    private List<Persistence> persistenceList;

    @OneToMany(mappedBy = "person", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    private List<LoginHistory> loginHistoryList;

    @OneToMany(mappedBy = "person", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    private List<AuthenticationToken> authenticationTokenList;

    @OneToMany(mappedBy = "person", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    private List<BackupCode> backupCodeList;

    @OneToOne
    @JoinColumn(name = "LatestLoginHistoryID")
    private LoginHistory latestLoginHistory;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @MapsId
    @JoinColumn(name = "AddressID")
    private Address address;

    @Transient
    private String persianLoginTime;
}
