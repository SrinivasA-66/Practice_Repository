package com.cts.openbankx.model;

import com.cts.openbankx.enums.TokenStatus;
import com.cts.openbankx.enums.TokenType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = true)
    private AuthClient authClient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType;

    private String scope;

    @Column(columnDefinition = "TEXT")
    private String tokenValue;

    private String endpoint;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenStatus status;

    public Token() {}

    public Token(Long tokenId, AuthClient authClient, User user, TokenType tokenType, String scope, String tokenValue, String endpoint, LocalDateTime issuedAt, LocalDateTime expiresAt, TokenStatus status) {
        this.tokenId = tokenId;
        this.authClient = authClient;
        this.user = user;
        this.tokenType = tokenType;
        this.scope = scope;
        this.tokenValue = tokenValue;
        this.endpoint = endpoint;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.status = status;
    }

    public Long getTokenId() { return tokenId; }
    public void setTokenId(Long tokenId) { this.tokenId = tokenId; }

    public AuthClient getAuthClient() { return authClient; }
    public void setAuthClient(AuthClient authClient) { this.authClient = authClient; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public TokenType getTokenType() { return tokenType; }
    public void setTokenType(TokenType tokenType) { this.tokenType = tokenType; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getTokenValue() { return tokenValue; }
    public void setTokenValue(String tokenValue) { this.tokenValue = tokenValue; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public TokenStatus getStatus() { return status; }
    public void setStatus(TokenStatus status) { this.status = status; }
}
