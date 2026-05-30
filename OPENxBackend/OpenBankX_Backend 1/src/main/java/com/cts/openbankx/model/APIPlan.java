package com.cts.openbankx.model;

import com.cts.openbankx.enums.Environment;
import com.cts.openbankx.enums.PlanDurationUnit;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "api_plan")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class APIPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private APIProduct apiProduct;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Environment environment;

    @Column(name = "rate_limit_per_min", nullable = false)
    private Integer rateLimitPerMin;

    @Column(name = "daily_quota", nullable = false)
    private Integer dailyQuota;

    @Column(nullable = false)
    private Integer sla;

    @Column(name = "duration_value", nullable = false)
    private Integer durationValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit", nullable = false)
    private PlanDurationUnit durationUnit;



    public APIProduct getApiProduct() {
        return apiProduct;
    }

    public void setApiProduct(APIProduct apiProduct) {
        this.apiProduct = apiProduct;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Integer getRateLimitPerMin() {
        return rateLimitPerMin;
    }

    public void setRateLimitPerMin(Integer rateLimitPerMin) {
        this.rateLimitPerMin = rateLimitPerMin;
    }

    public Integer getDailyQuota() {
        return dailyQuota;
    }

    public void setDailyQuota(Integer dailyQuota) {
        this.dailyQuota = dailyQuota;
    }

    public Integer getSla() {
        return sla;
    }

    public void setSla(Integer sla) {
        this.sla = sla;
    }

    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(Integer durationValue) {
        this.durationValue = durationValue;
    }

    public PlanDurationUnit getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(PlanDurationUnit durationUnit) {
        this.durationUnit = durationUnit;
    }
}