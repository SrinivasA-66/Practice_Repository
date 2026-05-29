package com.cts.openbankx.model;

import com.cts.openbankx.enums.AccountStatus;
import com.cts.openbankx.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "account_ref")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AccountRef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String accountNumberMasked;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;


    @Column(nullable = false)
    private Double balance;

    public Long getAccountId() { return accountId; }
    public User getUser() { return user; }
    public String getAccountNumberMasked() { return accountNumberMasked; }
    public AccountType getType() { return type; }
    public String getCurrency() { return currency; }
    public AccountStatus getStatus() { return status; }
    public Double getBalance() { return balance; }

    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public void setUser(User user) { this.user = user; }
    public void setAccountNumberMasked(String accountNumberMasked) { this.accountNumberMasked = accountNumberMasked; }
    public void setType(AccountType type) { this.type = type; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public void setBalance(Double balance) { this.balance = balance; }
}