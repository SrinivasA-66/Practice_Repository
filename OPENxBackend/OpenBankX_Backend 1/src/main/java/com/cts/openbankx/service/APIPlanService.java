package com.cts.openbankx.service;

import java.util.List;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.openbankx.enums.PlanDurationUnit;
import com.cts.openbankx.model.APIPlan;
import com.cts.openbankx.repository.APIPlanRepository;
import com.cts.openbankx.repository.TPPSubscriptionRepository;

@Service
@Transactional
public class APIPlanService {

    private final APIPlanRepository repo;
    private final TPPSubscriptionRepository subscriptionRepo;

    public APIPlanService(APIPlanRepository repo,
                          TPPSubscriptionRepository subscriptionRepo) {
        this.repo = repo;
        this.subscriptionRepo = subscriptionRepo;
    }


    @PostConstruct
    public void backfillLegacyDurations() {
        for (APIPlan p : repo.findAll()) {
            boolean dirty = false;
            if (p.getDurationValue() == null || p.getDurationValue() <= 0) {
                p.setDurationValue(1);
                dirty = true;
            }
            if (p.getDurationUnit() == null) {
                p.setDurationUnit(PlanDurationUnit.MONTHS);
                dirty = true;
            }
            if (dirty) repo.save(p);
        }
    }

    public List<APIPlan> findAll() {
        return repo.findAll();
    }

    public List<APIPlan> findByProduct(Long productId) {
        return repo.findByApiProduct_ProductId(productId);
    }


    public APIPlan save(APIPlan plan) {

        if (plan.getApiProduct() == null || plan.getApiProduct().getProductId() == null) {
            throw new IllegalArgumentException("apiProduct.productId is required.");
        }
        if (plan.getEnvironment() == null) {
            throw new IllegalArgumentException("environment is required.");
        }
        if (plan.getDurationValue() == null || plan.getDurationValue() <= 0) {
            // Default rather than fail — keeps the admin form forgiving.
            plan.setDurationValue(1);
        }
        if (plan.getDurationUnit() == null) {
            plan.setDurationUnit(PlanDurationUnit.MONTHS);
        }
        if (plan.getRateLimitPerMin() == null) plan.setRateLimitPerMin(60);
        if (plan.getDailyQuota() == null)     plan.setDailyQuota(1000);
        if (plan.getSla() == null)            plan.setSla(99);


        return repo.save(plan);
    }


    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("API Plan not found: " + id);
        }
        subscriptionRepo.deleteAllByPlanId(id);
        repo.deleteById(id);
    }
}
