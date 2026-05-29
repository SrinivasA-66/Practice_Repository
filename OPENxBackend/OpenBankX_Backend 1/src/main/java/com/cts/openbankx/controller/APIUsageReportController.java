package com.cts.openbankx.controller;

import com.cts.openbankx.model.APIUsageReport;
import com.cts.openbankx.service.APIUsageReportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class APIUsageReportController {

    private final APIUsageReportService reportService;

    public APIUsageReportController(APIUsageReportService reportService) {
        this.reportService = reportService;
    }


    @PostMapping("/reports/apps/{tppAppId}")
    public APIUsageReport generateReport(@PathVariable Long tppAppId) {
        return reportService.generateReportForApp(tppAppId);
    }


    @GetMapping("/apps/{id}/stats")
    public APIUsageReport getAppStats(@PathVariable Long id) {
        return reportService.getLatestReportForApp(id);
    }
}