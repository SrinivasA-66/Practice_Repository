package com.cts.openbankx.service;

import com.cts.openbankx.model.APILog;
import com.cts.openbankx.model.APIUsageReport;
import com.cts.openbankx.repository.APILogRepository;
import com.cts.openbankx.repository.APIUsageReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class APIUsageReportService {

    private final APILogRepository apiLogRepo;
    private final APIUsageReportRepository reportRepo;

    public APIUsageReportService(APILogRepository apiLogRepo,
                                 APIUsageReportRepository reportRepo) {
        this.apiLogRepo = apiLogRepo;
        this.reportRepo = reportRepo;
    }


    public APIUsageReport generateReportForApp(Long tppAppId) {

        List<APILog> logs =
                apiLogRepo.findByTppApp_TppAppId(tppAppId);

        long totalCalls = logs.size();
        long errorCalls = logs.stream()
                .filter(l -> l.getStatusCode() >= 400)
                .count();

        long throttleEvents = logs.stream()
                .filter(l -> l.getStatusCode() == 429)
                .count();

        double latencyP95 = logs.stream()
                .mapToInt(APILog::getLatencyMs)
                .sorted()
                .skip((long) (totalCalls * 0.95))
                .findFirst()
                .orElse(0);

        String metrics = String.format("""
        {
          "calls": %d,
          "errors": %d,
          "errorRate": "%.2f%%",
          "latencyP95Ms": %.0f,
          "throttleEvents": %d
        }
        """,
            totalCalls,
            errorCalls,
            totalCalls > 0 ? (errorCalls * 100.0 / totalCalls) : 0,
            latencyP95,
            throttleEvents
        );

        APIUsageReport report = new APIUsageReport();
        report.setScope("TPP_APP:" + tppAppId);
        report.setMetrics(metrics);

        return reportRepo.save(report);
    }


    public APIUsageReport getLatestReportForApp(Long tppAppId) {

        String scope = "TPP_APP:" + tppAppId;

        return reportRepo
            .findTopByScopeOrderByGeneratedDateDesc(scope)
            .orElseGet(() -> generateReportForApp(tppAppId));
    }
}
