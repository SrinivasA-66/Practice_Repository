package com.cts.openbankx.model;

import com.cts.openbankx.enums.IncidentCategory;
import com.cts.openbankx.enums.IncidentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incident")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incidentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentCategory category;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime detectedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    public Incident() {}

    public Incident(Long incidentId, IncidentCategory category, String description, LocalDateTime detectedDate, IncidentStatus status) {
        this.incidentId = incidentId;
        this.category = category;
        this.description = description;
        this.detectedDate = detectedDate;
        this.status = status;
    }

    public Long getIncidentId() { return incidentId; }
    public void setIncidentId(Long incidentId) { this.incidentId = incidentId; }

    public IncidentCategory getCategory() { return category; }
    public void setCategory(IncidentCategory category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDetectedDate() { return detectedDate; }
    public void setDetectedDate(LocalDateTime detectedDate) { this.detectedDate = detectedDate; }

    public IncidentStatus getStatus() { return status; }
    public void setStatus(IncidentStatus status) { this.status = status; }
}
