package ir.tic.clouddc.resource;

import ir.tic.clouddc.event.Event;
import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
@Getter
@Setter
public final class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ModuleGenerator")
    @SequenceGenerator(name = "ModuleGenerator", sequenceName = "Module_SEQ", allocationSize = 1, schema = "Resource", initialValue = 1000)
    @Column(name = "ModuleID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ModuleCategoryID", nullable = false)
    private ModuleCategory moduleCategory;

    @Column(name = "SerialNumber", unique = true, nullable = false)
    private String serialNumber;

    @Column(name = "Active", nullable = false)
    private boolean active;

    @Column(name = "Problematic", nullable = false)
    private boolean problematic;

    @Column(name = "Spare", nullable = false)
    private boolean spare;

    @Column(name = "LocalityId", nullable = false)
    private long localityId; // Spare: true -> RoomID, false -> DeviceID

    @ManyToMany(mappedBy = "moduleList")
    private List<Event> moduleEventList;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "PersistenceID")
    private Persistence persistence;
}
