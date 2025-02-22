package ir.tic.clouddc.report;

public interface ReportService {
    DailyReport getReferencedTodayReport(long todayReportID);
}
