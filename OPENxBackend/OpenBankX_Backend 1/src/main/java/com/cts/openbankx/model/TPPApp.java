package com.cts.openbankx.model;

import com.cts.openbankx.enums.AppStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "tpp_app")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TPPApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tppAppId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tpp_id", nullable = false)
    private TPP tpp;

    @Column(nullable = false)
    private String appName;

    @Column(columnDefinition = "TEXT")
    private String redirectURIs;

    @Column(columnDefinition = "TEXT")
    private String publicKeysJWKSet;

    @Column(columnDefinition = "TEXT")
    private String scopesRequested;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppStatus status;

    public TPPApp() {}

    public TPPApp(Long tppAppId, TPP tpp, String appName, String redirectURIs, String publicKeysJWKSet, String scopesRequested, AppStatus status) {
        this.tppAppId = tppAppId;
        this.tpp = tpp;
        this.appName = appName;
        this.redirectURIs = redirectURIs;
        this.publicKeysJWKSet = publicKeysJWKSet;
        this.scopesRequested = scopesRequested;
        this.status = status;
    }

    public Long getTppAppId() { return tppAppId; }
    public void setTppAppId(Long tppAppId) { this.tppAppId = tppAppId; }

    public TPP getTpp() { return tpp; }
    public void setTpp(TPP tpp) { this.tpp = tpp; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getRedirectURIs() { return redirectURIs; }
    public void setRedirectURIs(String redirectURIs) { this.redirectURIs = redirectURIs; }

    public String getPublicKeysJWKSet() { return publicKeysJWKSet; }
    public void setPublicKeysJWKSet(String publicKeysJWKSet) { this.publicKeysJWKSet = publicKeysJWKSet; }

    public String getScopesRequested() { return scopesRequested; }
    public void setScopesRequested(String scopesRequested) { this.scopesRequested = scopesRequested; }

    public AppStatus getStatus() { return status; }
    public void setStatus(AppStatus status) { this.status = status; }
}
