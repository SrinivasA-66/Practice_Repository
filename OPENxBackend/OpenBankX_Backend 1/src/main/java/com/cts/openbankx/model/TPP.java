package com.cts.openbankx.model;

import com.cts.openbankx.enums.TPPStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "tpp")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TPP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tppId;

    @Column(nullable = false)
    private String legalName;

    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @Column(columnDefinition = "TEXT")
    private String contactInfo;

    private String certificationRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TPPStatus status;


    @Column(name = "owner_email")
    private String ownerEmail;

    public TPP() {}

    public TPP(Long tppId, String legalName, String registrationNumber, String contactInfo, String certificationRef, TPPStatus status) {
        this.tppId = tppId;
        this.legalName = legalName;
        this.registrationNumber = registrationNumber;
        this.contactInfo = contactInfo;
        this.certificationRef = certificationRef;
        this.status = status;
    }

    public Long getTppId() { return tppId; }
    public void setTppId(Long tppId) { this.tppId = tppId; }

    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getCertificationRef() { return certificationRef; }
    public void setCertificationRef(String certificationRef) { this.certificationRef = certificationRef; }

    public TPPStatus getStatus() { return status; }
    public void setStatus(TPPStatus status) { this.status = status; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
}
