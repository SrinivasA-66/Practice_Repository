package com.cts.openbankx.model;

import com.cts.openbankx.enums.LimitWindow;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rate_limit_counter")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RateLimitCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long counterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tpp_app_id", nullable = false)
    private TPPApp tppApp;

    @Column(nullable = false)
    private LocalDateTime periodStart;

    @Column(nullable = false)
    private Integer count;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LimitWindow limitWindow;

    public RateLimitCounter() {}

    public RateLimitCounter(Long counterId, TPPApp tppApp, LocalDateTime periodStart, Integer count, LimitWindow limitWindow) {
        this.counterId = counterId;
        this.tppApp = tppApp;
        this.periodStart = periodStart;
        this.count = count;
        this.limitWindow = limitWindow;
    }

    public Long getCounterId() { return counterId; }
    public void setCounterId(Long counterId) { this.counterId = counterId; }

    public TPPApp getTppApp() { return tppApp; }
    public void setTppApp(TPPApp tppApp) { this.tppApp = tppApp; }

    public LocalDateTime getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }

    public LimitWindow getLimitWindow() { return limitWindow; }
    public void setLimitWindow(LimitWindow limitWindow) { this.limitWindow = limitWindow; }
}
