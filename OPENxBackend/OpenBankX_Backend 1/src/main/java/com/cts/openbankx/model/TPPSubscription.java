package com.cts.openbankx.model;

import com.cts.openbankx.enums.SubscriptionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tpp_subscription")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TPPSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tpp_app_id", nullable = false)
    private TPPApp tppApp;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private APIPlan apiPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(name = "subscribed_date")
    private LocalDateTime subscribedDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    public Long getSubscriptionId() { return subscriptionId; }
    public TPPApp getTppApp() { return tppApp; }
    public APIPlan getApiPlan() { return apiPlan; }
    public SubscriptionStatus getStatus() { return status; }
    public LocalDateTime getSubscribedDate() { return subscribedDate; }
    public LocalDateTime getExpiryDate() { return expiryDate; }

    public void setTppApp(TPPApp tppApp) { this.tppApp = tppApp; }
    public void setApiPlan(APIPlan apiPlan) { this.apiPlan = apiPlan; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
    public void setSubscribedDate(LocalDateTime subscribedDate) { this.subscribedDate = subscribedDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
}
