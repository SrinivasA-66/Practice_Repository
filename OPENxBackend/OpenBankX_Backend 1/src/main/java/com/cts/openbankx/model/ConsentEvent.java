package com.cts.openbankx.model;

import com.cts.openbankx.enums.ConsentEventType;
import com.cts.openbankx.enums.PerformedBy;
import jakarta.persistence.*;
//import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consent_event")

public class ConsentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long consentEventId;

    @ManyToOne(optional = false)
    private Consent consent;

    @Enumerated(EnumType.STRING)
    private ConsentEventType eventType;

    private LocalDateTime eventDate;

    @Enumerated(EnumType.STRING)
    private PerformedBy performedBy;

    private String notes;

	public Long getConsentEventId() {
		return consentEventId;
	}

	public void setConsentEventId(Long consentEventId) {
		this.consentEventId = consentEventId;
	}

	public Consent getConsent() {
		return consent;
	}

	public void setConsent(Consent consent) {
		this.consent = consent;
	}

	public ConsentEventType getEventType() {
		return eventType;
	}

	public void setEventType(ConsentEventType eventType) {
		this.eventType = eventType;
	}

	public LocalDateTime getEventDate() {
		return eventDate;
	}

	public void setEventDate(LocalDateTime eventDate) {
		this.eventDate = eventDate;
	}

	public PerformedBy getPerformedBy() {
		return performedBy;
	}

	public void setPerformedBy(PerformedBy performedBy) {
		this.performedBy = performedBy;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
    
    
}