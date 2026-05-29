package com.cts.openbankx.gateway;

import com.cts.openbankx.enums.IncidentCategory;
import com.cts.openbankx.enums.IncidentStatus;
import com.cts.openbankx.model.APILog;
import com.cts.openbankx.model.Incident;
import com.cts.openbankx.model.TPPApp;
import com.cts.openbankx.repository.APILogRepository;
import com.cts.openbankx.repository.IncidentRepository;
import com.cts.openbankx.repository.TPPAppRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class ApiGatewayFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiGatewayFilter.class);

    private final APILogRepository logRepo;
    private final TPPAppRepository tppAppRepo;
    private final IncidentRepository incidentRepo;

    private final ConcurrentHashMap<String, AtomicInteger> rateLimit = new ConcurrentHashMap<>();
    private volatile long windowStart = System.currentTimeMillis();
    private static final int  MAX_REQUESTS_PER_MINUTE = 500;
    private static final long WINDOW_MS = 60_000;


    private static final int LATENCY_INCIDENT_THRESHOLD_MS = 3000;

    public ApiGatewayFilter(APILogRepository logRepo,
                            TPPAppRepository tppAppRepo,
                            IncidentRepository incidentRepo) {
        this.logRepo = logRepo;
        this.tppAppRepo = tppAppRepo;
        this.incidentRepo = incidentRepo;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {


        long now = System.currentTimeMillis();
        if (now - windowStart > WINDOW_MS) {
            rateLimit.clear();
            windowStart = now;
        }
        String clientIp = request.getRemoteAddr();
        AtomicInteger counter = rateLimit.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
        if (counter.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Try again shortly.\"}");
            persistThrottleLog(request, 429, 0L);
            raiseIncident(IncidentCategory.LATENCY,
                "Rate-limit hit (429) from " + clientIp + " for " + request.getRequestURI());
            return;
        }

        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        long latency = System.currentTimeMillis() - start;


        if (isTppApiCall(request.getRequestURI())) {
            persistThrottleLog(request, response.getStatus(), latency);


            int status = response.getStatus();
            String appName = lookupAppName(request);
            String descBase = (appName != null ? "[" + appName + "] " : "")
                            + request.getMethod() + " " + request.getRequestURI();

            if (status >= 500) {
                raiseIncident(IncidentCategory.OUTAGE,
                    "Server error " + status + " on " + descBase);
            } else if (status == 429) {
                raiseIncident(IncidentCategory.LATENCY,
                    "Rate-limit (429) on " + descBase);
            } else if (status == 401 || status == 403) {
                raiseIncident(IncidentCategory.SECURITY,
                    "Unauthorized (" + status + ") on " + descBase);
            } else if (status >= 400) {
                raiseIncident(IncidentCategory.OUTAGE,
                    "Client error " + status + " on " + descBase);
            } else if (latency > LATENCY_INCIDENT_THRESHOLD_MS) {
                raiseIncident(IncidentCategory.LATENCY,
                    "Slow response (" + latency + " ms) on " + descBase);
            }
        }
    }

    private void persistThrottleLog(HttpServletRequest request, int status, long latency) {
        try {
            APILog entry = new APILog();
            entry.setEndpoint(request.getRequestURI());
            entry.setMethod(request.getMethod());
            entry.setStatusCode(status);
            entry.setLatencyMs((int) latency);
            entry.setTimestamp(LocalDateTime.now());

            String tppAppIdHeader = request.getHeader("X-TPP-App-Id");
            if (tppAppIdHeader != null && !tppAppIdHeader.isBlank()) {
                try {
                    Long appId = Long.parseLong(tppAppIdHeader);
                    Optional<TPPApp> app = tppAppRepo.findById(appId);
                    app.ifPresent(entry::setTppApp);
                } catch (NumberFormatException ignored) {}
            }
            logRepo.save(entry);
        } catch (Exception e) {
            log.warn("Failed to persist api_log entry: {}", e.getMessage());
        }
    }

    private String lookupAppName(HttpServletRequest request) {
        String tppAppIdHeader = request.getHeader("X-TPP-App-Id");
        if (tppAppIdHeader == null || tppAppIdHeader.isBlank()) return null;
        try {
            return tppAppRepo.findById(Long.parseLong(tppAppIdHeader))
                .map(TPPApp::getAppName).orElse(null);
        } catch (Exception ignored) {
            return null;
        }
    }


    private void raiseIncident(IncidentCategory category, String description) {
        try {
            boolean alreadyOpen = incidentRepo.findByStatus(IncidentStatus.OPEN).stream()
                .anyMatch(i -> i.getCategory() == category
                            && description.equals(i.getDescription()));
            if (alreadyOpen) return;

            Incident inc = new Incident();
            inc.setCategory(category);
            inc.setDescription(description);
            inc.setDetectedDate(LocalDateTime.now());
            inc.setStatus(IncidentStatus.OPEN);
            incidentRepo.save(inc);
        } catch (Exception e) {
            log.warn("Failed to persist incident: {}", e.getMessage());
        }
    }


    private boolean isTppApiCall(String uri) {
        if (uri == null) return false;
        return uri.startsWith("/api/v1/aisp/")
            || uri.startsWith("/api/v1/pisp/")
            || uri.startsWith("/api/v1/cbpii/");
    }
}
