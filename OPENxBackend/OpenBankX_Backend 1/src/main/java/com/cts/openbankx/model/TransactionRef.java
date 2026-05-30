package com.cts.openbankx.model;

import com.cts.openbankx.enums.TxnType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_ref")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TransactionRef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long txnRefId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountRef accountRef;

    @Column(nullable = false)
    private LocalDateTime txnDate;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TxnType txnType;

    private String narrative;

    public TransactionRef() {}

    public TransactionRef(Long txnRefId, AccountRef accountRef, LocalDateTime txnDate, BigDecimal amount, TxnType txnType, String narrative) {
        this.txnRefId = txnRefId;
        this.accountRef = accountRef;
        this.txnDate = txnDate;
        this.amount = amount;
        this.txnType = txnType;
        this.narrative = narrative;
    }

    public Long getTxnRefId() { return txnRefId; }
    public void setTxnRefId(Long txnRefId) { this.txnRefId = txnRefId; }

    public AccountRef getAccountRef() { return accountRef; }
    public void setAccountRef(AccountRef accountRef) { this.accountRef = accountRef; }

    public LocalDateTime getTxnDate() { return txnDate; }
    public void setTxnDate(LocalDateTime txnDate) { this.txnDate = txnDate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public TxnType getTxnType() { return txnType; }
    public void setTxnType(TxnType txnType) { this.txnType = txnType; }

    public String getNarrative() { return narrative; }
    public void setNarrative(String narrative) { this.narrative = narrative; }
}
