package com.navi.dcim.report;

import com.navi.dcim.report.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<DailyReport, Integer> {

    DailyReport findByActive(boolean active);
}
