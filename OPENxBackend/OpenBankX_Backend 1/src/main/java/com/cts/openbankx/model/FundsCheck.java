package com.cts.openbankx.model;

import com.cts.openbankx.enums.FundsCheckResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "funds_check")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FundsCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fundsCheckId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tpp_app_id", nullable = false)
    private TPPApp tppApp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountRef accountRef;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FundsCheckResult result;

    @Column(nullable = false)
    private LocalDateTime checkedDate;

    public FundsCheck() {}

    public FundsCheck(Long fundsCheckId, TPPApp tppApp, AccountRef accountRef, BigDecimal amount, String currency, FundsCheckResult result, LocalDateTime checkedDate) {
        this.fundsCheckId = fundsCheckId;
        this.tppApp = tppApp;
        this.accountRef = accountRef;
        this.amount = amount;
        this.currency = currency;
        this.result = result;
        this.checkedDate = checkedDate;
    }

    public Long getFundsCheckId() { return fundsCheckId; }
    public void setFundsCheckId(Long fundsCheckId) { this.fundsCheckId = fundsCheckId; }

    public TPPApp getTppApp() { return tppApp; }
    public void setTppApp(TPPApp tppApp) { this.tppApp = tppApp; }

    public AccountRef getAccountRef() { return accountRef; }
    public void setAccountRef(AccountRef accountRef) { this.accountRef = accountRef; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public FundsCheckResult getResult() { return result; }
    public void setResult(FundsCheckResult result) { this.result = result; }

    public LocalDateTime getCheckedDate() { return checkedDate; }
    public void setCheckedDate(LocalDateTime checkedDate) { this.checkedDate = checkedDate; }
}
