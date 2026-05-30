package com.cts.openbankx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_usage_report")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class APIUsageReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;


    @Column(nullable = false)
    private String scope;


    @Column(columnDefinition = "TEXT")
    private String metrics;

    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime generatedDate;

    public APIUsageReport() {}

    @PrePersist
    protected void onCreate() {
        this.generatedDate = LocalDateTime.now();
    }

    public Long getReportId() {
        return reportId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }
}