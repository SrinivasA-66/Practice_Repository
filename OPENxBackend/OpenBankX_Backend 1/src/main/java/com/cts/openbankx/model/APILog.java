package com.cts.openbankx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_log")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class APILog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long apiLogId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tpp_app_id", nullable = true)
    private TPPApp tppApp;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = true)
    private AuthClient authClient;

    @Column(nullable = false)
    private String endpoint;


    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private Integer statusCode;

    @Column(nullable = false)
    private Integer latencyMs;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public APILog() {}

    public APILog(Long apiLogId, TPPApp tppApp, AuthClient authClient,
                  String endpoint, String method,
                  Integer statusCode, Integer latencyMs,
                  LocalDateTime timestamp) {
        this.apiLogId = apiLogId;
        this.tppApp = tppApp;
        this.authClient = authClient;
        this.endpoint = endpoint;
        this.method = method;
        this.statusCode = statusCode;
        this.latencyMs = latencyMs;
        this.timestamp = timestamp;
    }

    public Long getApiLogId() { return apiLogId; }
    public void setApiLogId(Long apiLogId) { this.apiLogId = apiLogId; }

    public TPPApp getTppApp() { return tppApp; }
    public void setTppApp(TPPApp tppApp) { this.tppApp = tppApp; }

    public AuthClient getAuthClient() { return authClient; }
    public void setAuthClient(AuthClient authClient) { this.authClient = authClient; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }

    public Integer getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Integer latencyMs) { this.latencyMs = latencyMs; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}