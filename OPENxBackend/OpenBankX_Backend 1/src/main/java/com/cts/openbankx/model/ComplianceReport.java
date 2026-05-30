package com.cts.openbankx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_report")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ComplianceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long compReportId;

    @Column(nullable = false)
    private String scope;

    @Column(columnDefinition = "TEXT")
    private String metrics;

    @Column(nullable = false)
    private LocalDateTime generatedDate;

    public ComplianceReport() {}

    public ComplianceReport(Long compReportId, String scope, String metrics, LocalDateTime generatedDate) {
        this.compReportId = compReportId;
        this.scope = scope;
        this.metrics = metrics;
        this.generatedDate = generatedDate;
    }

    public Long getCompReportId() { return compReportId; }
    public void setCompReportId(Long compReportId) { this.compReportId = compReportId; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getMetrics() { return metrics; }
    public void setMetrics(String metrics) { this.metrics = metrics; }

    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }
}
