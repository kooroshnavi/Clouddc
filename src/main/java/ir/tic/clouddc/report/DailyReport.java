package ir.tic.clouddc.report;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(schema = "Report")
@NoArgsConstructor
@Getter
@Setter
public final class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReportID")
    private Long id;

    @Column(name = "Date", unique = true, nullable = false)
    private LocalDate date;

    @Column(name = "Active", nullable = false)
    private boolean active;

    @Transient
    private String persianDate;
}
