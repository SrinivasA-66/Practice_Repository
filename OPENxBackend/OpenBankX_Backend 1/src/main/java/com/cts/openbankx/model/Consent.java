package com.cts.openbankx.model;

import com.cts.openbankx.enums.ConsentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consent")
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long consentId;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private TPPApp tppApp;

    @Column(columnDefinition = "TEXT")
    private String scopeJSON;

    @Column(columnDefinition = "TEXT")
    private String resourceFilterJSON;

    private LocalDateTime createdDate;

    private LocalDateTime expiryDate;


@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 50)
private ConsentStatus status;


    public Long getConsentId() { return consentId; }
    public void setConsentId(Long consentId) { this.consentId = consentId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public TPPApp getTppApp() { return tppApp; }
    public void setTppApp(TPPApp tppApp) { this.tppApp = tppApp; }

    public String getScopeJSON() { return scopeJSON; }
    public void setScopeJSON(String scopeJSON) { this.scopeJSON = scopeJSON; }

    public String getResourceFilterJSON() { return resourceFilterJSON; }
    public void setResourceFilterJSON(String resourceFilterJSON) { this.resourceFilterJSON = resourceFilterJSON; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public ConsentStatus getStatus() { return status; }
    public void setStatus(ConsentStatus status) { this.status = status; }
}