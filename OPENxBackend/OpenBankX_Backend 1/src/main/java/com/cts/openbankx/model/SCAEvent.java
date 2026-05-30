package com.cts.openbankx.model;

import com.cts.openbankx.enums.SCAMethod;
import com.cts.openbankx.enums.SCAResult;
import jakarta.persistence.*;
//import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sca_event")

public class SCAEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scaEventId;

    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private SCAMethod method;

    @Enumerated(EnumType.STRING)
    private SCAResult result;

    private LocalDateTime eventTime;

    private String referenceId;

	public Long getScaEventId() {
		return scaEventId;
	}

	public void setScaEventId(Long scaEventId) {
		this.scaEventId = scaEventId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SCAMethod getMethod() {
		return method;
	}

	public void setMethod(SCAMethod method) {
		this.method = method;
	}

	public SCAResult getResult() {
		return result;
	}

	public void setResult(SCAResult result) {
		this.result = result;
	}

	public LocalDateTime getEventTime() {
		return eventTime;
	}

	public void setEventTime(LocalDateTime eventTime) {
		this.eventTime = eventTime;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
    
    
}