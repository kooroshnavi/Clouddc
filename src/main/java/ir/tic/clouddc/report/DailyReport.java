package ir.tic.clouddc.report;

import ir.tic.clouddc.event.Event;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(schema = "Report")
@NoArgsConstructor
@Data
public final class DailyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private LocalDate date;

    @Column
    private boolean active;

    @ManyToMany
    @JoinTable(name = "report_event", schema = "report", joinColumns = @JoinColumn(name = "report_id"), inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> eventList;

    @Transient
    private String persianDate;
}
