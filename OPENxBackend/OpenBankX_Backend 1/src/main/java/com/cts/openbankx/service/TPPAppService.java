package com.cts.openbankx.service;

import com.cts.openbankx.enums.AppStatus;
import com.cts.openbankx.enums.NotificationCategory;
import com.cts.openbankx.enums.ProductStatus;
import com.cts.openbankx.model.APIProduct;
import com.cts.openbankx.model.TPPApp;
import com.cts.openbankx.repository.APIProductRepository;
import com.cts.openbankx.repository.TPPAppRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TPPAppService {

    private static final Logger log = LoggerFactory.getLogger(TPPAppService.class);

    private final TPPAppRepository repo;
    private final NotificationService notificationService;
    private final APIProductRepository productRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    public TPPAppService(TPPAppRepository repo,
                         NotificationService notificationService,
                         APIProductRepository productRepo) {
        this.repo = repo;
        this.notificationService = notificationService;
        this.productRepo = productRepo;
    }

    public TPPApp register(TPPApp app) {

        String name = app.getAppName() == null ? "" : app.getAppName().trim();
        Long tppId = app.getTpp() != null ? app.getTpp().getTppId() : null;
        if (!name.isEmpty() && tppId != null) {
            boolean clash = repo.findByTpp_TppId(tppId).stream()
                    .filter(existing -> existing.getAppName() != null)
                    .anyMatch(existing -> existing.getAppName().trim().equalsIgnoreCase(name));
            if (clash) {
                throw new IllegalArgumentException(
                        "An app named '" + name + "' is already registered for this TPP. " +
                        "Choose a different name.");
            }
        }

        TPPApp saved = repo.save(app);

        notificationService.notifyAdmin(
                "New TPP app '" + saved.getAppName() + "' registered — awaiting approval",
                NotificationCategory.USAGE);

        return saved;
    }

    public List<TPPApp> findAll() {
        return repo.findAll();
    }

    public TPPApp findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("TPPApp not found: " + id));
    }

    public List<TPPApp> findByTpp(Long tppId) {
        return repo.findByTpp_TppId(tppId);
    }

    public TPPApp save(TPPApp app) {

        if (app.getRedirectURIs() == null || app.getRedirectURIs().isEmpty()) {
            throw new RuntimeException("Redirect URI is mandatory");
        }

        if (app.getPublicKeysJWKSet() == null || app.getPublicKeysJWKSet().isEmpty()) {
            throw new RuntimeException("Public JWK Set is mandatory");
        }

        if (app.getScopesRequested() == null || app.getScopesRequested().isEmpty()) {
            throw new RuntimeException("Scopes are mandatory");
        }

        return repo.save(app);
    }


    public TPPApp approve(Long id) {
        TPPApp app = findById(id);

        if (app.getStatus() == AppStatus.APPROVED) {

            ensureApiProductExists(app);
            return app;
        }

        app.setStatus(AppStatus.APPROVED);
        TPPApp saved = repo.save(app);

        try {
            ensureApiProductExists(saved);
        } catch (Exception ex) {

            log.warn("Auto-creation of API Product for app '{}' failed: {}",
                    saved.getAppName(), ex.getMessage());
        }

        notificationService.notifyAdmin(
                "App '" + saved.getAppName() + "' has been APPROVED and published as an API Product",
                NotificationCategory.USAGE);

        return saved;
    }


    public TPPApp reject(Long id) {
        TPPApp app = findById(id);

        if (app.getStatus() == AppStatus.REJECTED) {
            return app; // idempotent
        }

        app.setStatus(AppStatus.REJECTED);
        TPPApp saved = repo.save(app);

        notificationService.notifyAdmin(
                "App '" + saved.getAppName() + "' has been REJECTED",
                NotificationCategory.USAGE);

        return saved;
    }

    public void deleteIfPending(Long id) {
        TPPApp app = findById(id);

        if (app.getStatus() != AppStatus.PENDING) {
            throw new RuntimeException("Only PENDING apps can be deleted");
        }

        repo.delete(app);
    }


    private void ensureApiProductExists(TPPApp app) {
        String productName = app.getAppName();
        if (productName == null || productName.isBlank()) {
            return;
        }

        if (productRepo.findByName(productName).isPresent()) {
            return;
        }

        APIProduct product = new APIProduct();
        product.setName(productName);
        product.setDescription(buildDescription(app));
        product.setEndpointsJSON(buildEndpointsJson(app));
        product.setStatus(ProductStatus.ACTIVE);

        productRepo.save(product);
    }

    private String buildDescription(TPPApp app) {
        String tppName = (app.getTpp() != null && app.getTpp().getLegalName() != null)
                ? app.getTpp().getLegalName()
                : "an approved TPP";
        return "Auto-generated API Product for the approved TPP app '"
                + app.getAppName() + "' (provider: " + tppName + ").";
    }


    private String buildEndpointsJson(TPPApp app) {
        List<String> scopes = parseScopes(app.getScopesRequested());
        Map<String, List<String>> scopeToEndpoints = defaultScopeMap();

        List<String> endpoints = new ArrayList<>();
        for (String scope : scopes) {
            List<String> mapped = scopeToEndpoints.get(scope.toLowerCase());
            if (mapped != null) {
                for (String ep : mapped) {
                    if (!endpoints.contains(ep)) endpoints.add(ep);
                }
            } else {
                String fallback = "/" + scope;
                if (!endpoints.contains(fallback)) endpoints.add(fallback);
            }
        }

        try {
            return mapper.writeValueAsString(endpoints);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<String> parseScopes(String scopesJson) {
        if (scopesJson == null || scopesJson.isBlank()) return new ArrayList<>();
        try {
            String[] arr = mapper.readValue(scopesJson, String[].class);
            return new ArrayList<>(Arrays.asList(arr));
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    private Map<String, List<String>> defaultScopeMap() {
        Map<String, List<String>> m = new LinkedHashMap<>();
        m.put("accounts",            Arrays.asList("/aisp/accounts", "/aisp/accounts/{id}/txns"));
        m.put("balances",            Arrays.asList("/aisp/balances"));
        m.put("payments",            Arrays.asList("/pisp/payments"));
        m.put("funds-confirmations", Arrays.asList("/cbpii/funds-check"));
        return m;
    }
}
