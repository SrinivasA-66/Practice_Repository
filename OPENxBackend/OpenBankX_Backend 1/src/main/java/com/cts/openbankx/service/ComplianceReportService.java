package com.cts.openbankx.service;

import com.cts.openbankx.model.ComplianceReport;
import com.cts.openbankx.repository.ComplianceReportRepository;
//import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service

public class ComplianceReportService {

    private final ComplianceReportRepository repo;
    public ComplianceReportService(ComplianceReportRepository repo) {
    	this.repo=repo;
    }

    public ComplianceReport generate(String scope, String metrics) {
        ComplianceReport report = new ComplianceReport();
        report.setScope(scope);
        report.setMetrics(metrics);
        report.setGeneratedDate(LocalDateTime.now());
        return repo.save(report);
    }

    public List<ComplianceReport> getAll() {
        return repo.findAll();
    }
}