package ir.tic.clouddc.event;


import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.Utilizer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@Getter
@Setter
public abstract class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EventGenerator")
    @SequenceGenerator(name = "EventGenerator", sequenceName = "Event_SEQ", allocationSize = 1, schema = "Event", initialValue = 1000)
    @Column(name = "EventID")
    private Long id;

    @Column(name = "Active")
    private boolean active;

    @Column(name = "RegisterDate")
    private LocalDate registerDate;

    @Column(name = "RegisterTime")
    private LocalTime registerTime;

    @Column(name = "EventDate")
    private LocalDate eventDate;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinColumn(name = "EventCategoryID")
    private EventCategory eventCategory;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EventDetailID")
    private EventDetail eventDetail;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "UtilizerDeviceBalance", schema = "Event",
            joinColumns = {@JoinColumn(name = "EventID", referencedColumnName = "EventID")})
    @MapKeyColumn(name = "UtilizerID")
    @Column(name = "Balance")
    private Map<Integer, Integer> utilizerBalance;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "EventLocation", schema = "Event",
            joinColumns = {@JoinColumn(name = "EventID")},
            inverseJoinColumns = {@JoinColumn(name = "LocationID")})
    private List<Location> locationList;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "EventDevice", schema = "Event",
            joinColumns = {@JoinColumn(name = "EventID")},
            inverseJoinColumns = {@JoinColumn(name = "DeviceID")})
    private List<Device> deviceList;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "EventUtilizer", schema = "Event",
            joinColumns = {@JoinColumn(name = "EventID")},
            inverseJoinColumns = {@JoinColumn(name = "UtilizerID")})
    private List<Utilizer> utilizerList;

    @Transient
    private String persianRegisterDate;

    @Transient
    private String persianRegisterDayTime;
}

