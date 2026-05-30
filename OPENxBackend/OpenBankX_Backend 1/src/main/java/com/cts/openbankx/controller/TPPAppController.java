package com.cts.openbankx.controller;

import com.cts.openbankx.enums.AppStatus;
import com.cts.openbankx.enums.SubscriptionStatus;
import com.cts.openbankx.model.TPPApp;
import com.cts.openbankx.model.TPPSubscription;
import com.cts.openbankx.service.TPPAppService;
import com.cts.openbankx.service.TPPSubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/apps")
public class TPPAppController {

    private final TPPAppService tppAppService;
    private final TPPSubscriptionService subscriptionService;

    public TPPAppController(TPPAppService tppAppService, TPPSubscriptionService subscriptionService) {
        this.tppAppService = tppAppService;
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public TPPApp register(@RequestBody TPPApp app) {
        return tppAppService.register(app);
    }

    @GetMapping
    public List<TPPApp> getAllApps() {
        return tppAppService.findAll();
    }


    @GetMapping("/available")
    public List<Map<String, Object>> getAvailableApps() {

        List<TPPApp> approvedApps = tppAppService.findAll().stream()
                .filter(app -> app.getStatus() == AppStatus.APPROVED)
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();

        for (TPPApp app : approvedApps) {

            List<TPPSubscription> subs = subscriptionService.findByTppApp(app.getTppAppId());
            List<TPPSubscription> activeSubs = subs.stream()
                    .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                    .collect(Collectors.toList());

            if (!activeSubs.isEmpty()) {
                Map<String, Object> appData = new LinkedHashMap<>();
                appData.put("tppAppId", app.getTppAppId());
                appData.put("appName", app.getAppName());
                appData.put("tppName", app.getTpp() != null ? app.getTpp().getLegalName() : "Unknown");
                appData.put("scopesRequested", app.getScopesRequested());
                appData.put("status", app.getStatus());


                List<Map<String, Object>> subscriptions = new ArrayList<>();
                for (TPPSubscription sub : activeSubs) {
                    Map<String, Object> subData = new LinkedHashMap<>();
                    subData.put("subscriptionId", sub.getSubscriptionId());
                    subData.put("planId", sub.getApiPlan().getPlanId());
                    subData.put("environment", sub.getApiPlan().getEnvironment());
                    subData.put("productName", sub.getApiPlan().getApiProduct().getName());
                    subData.put("endpointsJSON", sub.getApiPlan().getApiProduct().getEndpointsJSON());
                    subData.put("rateLimitPerMin", sub.getApiPlan().getRateLimitPerMin());
                    subscriptions.add(subData);
                }
                appData.put("subscriptions", subscriptions);

                result.add(appData);
            }
        }

        return result;
    }

    @GetMapping("/{id}")
    public TPPApp getApp(@PathVariable Long id) {
        return tppAppService.findById(id);
    }

    @PutMapping("/{id}")
    public TPPApp updateApp(@PathVariable Long id, @RequestBody TPPApp app) {
        TPPApp existing = tppAppService.findById(id);

        if (app.getAppName() != null) existing.setAppName(app.getAppName());
        if (app.getRedirectURIs() != null) existing.setRedirectURIs(app.getRedirectURIs());
        if (app.getPublicKeysJWKSet() != null) existing.setPublicKeysJWKSet(app.getPublicKeysJWKSet());
        if (app.getScopesRequested() != null) existing.setScopesRequested(app.getScopesRequested());


        if (app.getStatus() != null && app.getStatus() != existing.getStatus()) {
            switch (app.getStatus()) {
                case APPROVED: return tppAppService.approve(id);
                case REJECTED: return tppAppService.reject(id);
                case PENDING:
                default:       existing.setStatus(app.getStatus());
            }
        }

        return tppAppService.save(existing);
    }

    @PutMapping("/{id}/approve")
    public TPPApp approveApp(@PathVariable Long id) {
        return tppAppService.approve(id);
    }

    @PutMapping("/{id}/reject")
    public TPPApp rejectApp(@PathVariable Long id) {
        return tppAppService.reject(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApp(@PathVariable Long id) {
        tppAppService.deleteIfPending(id);
        return ResponseEntity.noContent().build();
    }
}