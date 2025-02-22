package ir.tic.clouddc.report;

import ir.tic.clouddc.resource.ModulePack;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(schema = "Report")
@NoArgsConstructor
@Getter
@Setter
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DailyReportID")
    private Long id;

    @Column(name = "Date")
    private LocalDate date;

    @Column(name = "Active")
    private boolean active;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "moduleInventoryDailyBalance", schema = "Report",
            joinColumns = {@JoinColumn(name = "ReportID", referencedColumnName = "DailyReportID")})
    @MapKeyColumn(name = "ModulePackID")
    @Column(name = "Balance")
    private Map<ModulePack, Integer> modulePackDailyBalanceMap;

    @Transient
    private String persianDateTime;
}
