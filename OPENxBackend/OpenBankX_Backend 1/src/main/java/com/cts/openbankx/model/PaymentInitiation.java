package com.cts.openbankx.model;

import com.cts.openbankx.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_initiation")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PaymentInitiation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tpp_app_id", nullable = false)
    private TPPApp tppApp;

    @Column(nullable = false)
    private String debtorAccountRef;

    @Column(nullable = false)
    private String creditorAccountRef;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    public PaymentInitiation() {}

    public PaymentInitiation(Long paymentId, TPPApp tppApp, String debtorAccountRef, String creditorAccountRef, BigDecimal amount, String currency, PaymentStatus status, LocalDateTime createdDate) {
        this.paymentId = paymentId;
        this.tppApp = tppApp;
        this.debtorAccountRef = debtorAccountRef;
        this.creditorAccountRef = creditorAccountRef;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdDate = createdDate;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public TPPApp getTppApp() { return tppApp; }
    public void setTppApp(TPPApp tppApp) { this.tppApp = tppApp; }

    public String getDebtorAccountRef() { return debtorAccountRef; }
    public void setDebtorAccountRef(String debtorAccountRef) { this.debtorAccountRef = debtorAccountRef; }

    public String getCreditorAccountRef() { return creditorAccountRef; }
    public void setCreditorAccountRef(String creditorAccountRef) { this.creditorAccountRef = creditorAccountRef; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    

    @JsonProperty("tppAppId")
       public void setTppAppId(Long tppAppId) {
           this.tppApp = new TPPApp();
           this.tppApp.setTppAppId(tppAppId);
       }

}
