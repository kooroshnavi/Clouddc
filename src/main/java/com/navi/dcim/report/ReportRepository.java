package com.navi.dcim.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<DailyReport, Integer> {

    Optional<DailyReport> findByActive(boolean active);
}
