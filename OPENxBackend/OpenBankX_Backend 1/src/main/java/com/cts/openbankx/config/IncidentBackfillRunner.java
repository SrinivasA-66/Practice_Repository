package com.cts.openbankx.config;

import com.cts.openbankx.enums.IncidentCategory;
import com.cts.openbankx.enums.IncidentStatus;
import com.cts.openbankx.model.APILog;
import com.cts.openbankx.model.Incident;
import com.cts.openbankx.repository.APILogRepository;
import com.cts.openbankx.repository.IncidentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Order(60)
public class IncidentBackfillRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IncidentBackfillRunner.class);


    private static final int LATENCY_THRESHOLD_MS = 3000;

    private final APILogRepository logRepo;
    private final IncidentRepository incidentRepo;

    public IncidentBackfillRunner(APILogRepository logRepo, IncidentRepository incidentRepo) {
        this.logRepo = logRepo;
        this.incidentRepo = incidentRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            int created = 0;
            java.util.Set<String> seenKeys = new java.util.HashSet<>();


            for (Incident inc : incidentRepo.findAll()) {
                seenKeys.add(inc.getCategory() + "|" + inc.getDescription());
            }

            for (APILog row : logRepo.findAll()) {
                String uri = row.getEndpoint();
                if (uri == null) continue;
                if (!(uri.startsWith("/api/v1/aisp/")
                   || uri.startsWith("/api/v1/pisp/")
                   || uri.startsWith("/api/v1/cbpii/"))) continue;

                int status = row.getStatusCode() == null ? 200 : row.getStatusCode();
                int latency = row.getLatencyMs() == null ? 0 : row.getLatencyMs();
                String appName = row.getTppApp() != null ? row.getTppApp().getAppName() : null;

                IncidentCategory category = null;
                String reason = null;

                if (status >= 500)          { category = IncidentCategory.OUTAGE;   reason = "Server error " + status; }
                else if (status == 429)     { category = IncidentCategory.LATENCY;  reason = "Rate-limit (429)"; }
                else if (status == 401
                      || status == 403)     { category = IncidentCategory.SECURITY; reason = "Unauthorized (" + status + ")"; }
                else if (status >= 400)     { category = IncidentCategory.OUTAGE;   reason = "Client error " + status; }
                else if (latency > LATENCY_THRESHOLD_MS) {
                                              category = IncidentCategory.LATENCY;  reason = "Slow response (" + latency + " ms)"; }

                if (category == null) continue;

                String desc = reason
                        + " on "
                        + (appName != null ? "[" + appName + "] " : "")
                        + row.getMethod() + " " + uri;
                String key = category + "|" + desc;
                if (seenKeys.contains(key)) continue;
                seenKeys.add(key);

                Incident inc = new Incident();
                inc.setCategory(category);
                inc.setDescription(desc);
                inc.setDetectedDate(row.getTimestamp() != null ? row.getTimestamp() : java.time.LocalDateTime.now());
                inc.setStatus(IncidentStatus.OPEN);
                incidentRepo.save(inc);
                created++;
            }

            log.info("[incident-backfill] Created {} missing incidents from api_log failures.", created);
        } catch (Exception e) {
            log.error("[incident-backfill] failed: {}", e.getMessage(), e);
        }
    }
}
