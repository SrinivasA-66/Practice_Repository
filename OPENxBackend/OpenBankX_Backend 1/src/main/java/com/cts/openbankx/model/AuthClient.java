package com.cts.openbankx.model;

import jakarta.persistence.*;
//import lombok.*;

@Entity
@Table(name = "auth_client")

public class AuthClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tpp_app_id")
    private TPPApp tppApp;

    @Column(columnDefinition = "TEXT")
    private String redirectURIs;

    @Column(columnDefinition = "TEXT")
    private String scopesAllowed;

    @Column(nullable = false)
    private String status; // ACTIVE / REVOKED

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public TPPApp getTppApp() {
		return tppApp;
	}

	public void setTppApp(TPPApp tppApp) {
		this.tppApp = tppApp;
	}

	public String getRedirectURIs() {
		return redirectURIs;
	}

	public void setRedirectURIs(String redirectURIs) {
		this.redirectURIs = redirectURIs;
	}

	public String getScopesAllowed() {
		return scopesAllowed;
	}

	public void setScopesAllowed(String scopesAllowed) {
		this.scopesAllowed = scopesAllowed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    
    
}