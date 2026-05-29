package com.cts.openbankx.service;

import com.cts.openbankx.enums.AppStatus;
import com.cts.openbankx.enums.NotificationCategory;
import com.cts.openbankx.enums.PlanDurationUnit;
import com.cts.openbankx.enums.SubscriptionStatus;
import com.cts.openbankx.model.APIPlan;
import com.cts.openbankx.model.TPPApp;
import com.cts.openbankx.model.TPPSubscription;
import com.cts.openbankx.repository.APIPlanRepository;
import com.cts.openbankx.repository.TPPAppRepository;
import com.cts.openbankx.repository.TPPSubscriptionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TPPSubscriptionService {

    private final TPPSubscriptionRepository repo;
    private final NotificationService notificationService;
    private final APIPlanRepository planRepo;
    private final TPPAppRepository appRepo;

    public TPPSubscriptionService(TPPSubscriptionRepository repo,
                                  NotificationService notificationService,
                                  APIPlanRepository planRepo,
                                  TPPAppRepository appRepo) {
        this.repo = repo;
        this.notificationService = notificationService;
        this.planRepo = planRepo;
        this.appRepo = appRepo;
    }


    @PostConstruct
    public void backfillNullDates() {
        try {
            for (TPPSubscription s : repo.findAll()) {
                boolean dirty = false;
                if (s.getSubscribedDate() == null) {
                    s.setSubscribedDate(LocalDateTime.now());
                    dirty = true;
                }
                if (s.getExpiryDate() == null) {
                    LocalDateTime start = s.getSubscribedDate() != null
                            ? s.getSubscribedDate() : LocalDateTime.now();
                    s.setExpiryDate(s.getApiPlan() != null
                            ? computeExpiry(start, s.getApiPlan())
                            : start.plusMonths(1));
                    dirty = true;
                }
                if (dirty) repo.save(s);
            }
        } catch (Exception ignored) {

        }
    }

    public List<TPPSubscription> findAll() {
        suspendExpired();
        return repo.findAll();
    }

    public List<TPPSubscription> findByTppApp(Long tppAppId) {
        suspendExpired();
        return repo.findByTppApp_TppAppId(tppAppId);
    }


    public TPPSubscription save(TPPSubscription sub) {

        Long planId = sub.getApiPlan() != null ? sub.getApiPlan().getPlanId() : null;
        Long appId  = sub.getTppApp()  != null ? sub.getTppApp().getTppAppId() : null;

        if (planId == null) {
            throw new IllegalArgumentException("apiPlan.planId is required to subscribe.");
        }
        if (appId == null) {
            throw new IllegalArgumentException("tppApp.tppAppId is required to subscribe.");
        }

        APIPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("API Plan not found: " + planId));
        TPPApp app = appRepo.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("TPP App not found: " + appId));

        if (app.getStatus() != AppStatus.APPROVED) {
            throw new IllegalArgumentException(
                "Only APPROVED apps can subscribe to a plan. Current status: " + app.getStatus());
        }


        if (sub.getSubscriptionId() == null) {
            boolean alreadySubscribed = repo.findByTppApp_TppAppId(appId).stream()
                    .anyMatch(existing ->
                            existing.getApiPlan() != null
                            && planId.equals(existing.getApiPlan().getPlanId())
                            && existing.getStatus() == SubscriptionStatus.ACTIVE);
            if (alreadySubscribed) {
                throw new IllegalArgumentException(
                        "App '" + app.getAppName() + "' is already subscribed to this plan. " +
                        "Cancel the existing subscription before subscribing again.");
            }
        }

        sub.setApiPlan(plan);
        sub.setTppApp(app);
        sub.setSubscribedDate(LocalDateTime.now());
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setExpiryDate(computeExpiry(sub.getSubscribedDate(), plan));

        TPPSubscription saved = repo.save(sub);

        String productName = plan.getApiProduct() != null ? plan.getApiProduct().getName() : "Product";
        notificationService.notifyAdmin(
                "App '" + app.getAppName() + "' subscribed to " + productName +
                        " plan — expires " + saved.getExpiryDate().toLocalDate(),
                NotificationCategory.USAGE);

        return saved;
    }

    private LocalDateTime computeExpiry(LocalDateTime start, APIPlan plan) {

        Integer rawValue = plan.getDurationValue();
        int value = (rawValue != null && rawValue > 0) ? rawValue : 1;

        PlanDurationUnit unit = plan.getDurationUnit() != null
                ? plan.getDurationUnit()
                : PlanDurationUnit.MONTHS;

        switch (unit) {
            case DAYS:   return start.plusDays(value);
            case YEARS:  return start.plusYears(value);
            case MONTHS:
            default:     return start.plusMonths(value);
        }
    }


    public TPPSubscription cancel(Long subscriptionId) {
        TPPSubscription sub = repo.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Subscription not found: " + subscriptionId));

        if (sub.getStatus() == SubscriptionStatus.CANCELLED) {
            return sub; // idempotent — already cancelled
        }

        sub.setStatus(SubscriptionStatus.CANCELLED);
        TPPSubscription saved = repo.save(sub);

        String appName = sub.getTppApp() != null ? sub.getTppApp().getAppName() : "app";
        String productName = sub.getApiPlan() != null && sub.getApiPlan().getApiProduct() != null
                ? sub.getApiPlan().getApiProduct().getName() : "plan";
        notificationService.notifyAdmin(
                "Subscription cancelled — '" + appName + "' / " + productName,
                NotificationCategory.USAGE);

        return saved;
    }

    private void suspendExpired() {
        List<TPPSubscription> subs = repo.findAll();
        for (TPPSubscription s : subs) {
            if (s.getStatus() == SubscriptionStatus.ACTIVE
                    && s.getExpiryDate() != null
                    && s.getExpiryDate().isBefore(LocalDateTime.now())) {

                s.setStatus(SubscriptionStatus.SUSPENDED);
                repo.save(s);
            }
        }
    }
}
