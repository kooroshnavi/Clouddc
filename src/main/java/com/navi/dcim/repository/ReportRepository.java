package com.navi.dcim.repository;

import com.navi.dcim.model.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<DailyReport, Integer> {

    DailyReport findByActive(boolean active);
}
