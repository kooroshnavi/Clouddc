package ir.tic.clouddc.report;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(schema = "Report")
@NoArgsConstructor
@Data
public final class DailyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReportID")
    private Long id;

    @Column(name = "Date")
    private LocalDate date;

    @Column(name = "Active")
    private boolean active;

    @Transient
    private String persianDate;
}
