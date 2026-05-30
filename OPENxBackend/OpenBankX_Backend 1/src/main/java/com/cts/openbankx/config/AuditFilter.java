package com.cts.openbankx.config;

import com.cts.openbankx.enums.ActorType;
import com.cts.openbankx.service.AuditTrailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class AuditFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuditFilter.class);

    private final AuditTrailService auditService;

    public AuditFilter(AuditTrailService auditService) {
        this.auditService = auditService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        chain.doFilter(request, response);


        try {
            if (shouldAudit(request)) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String actorId = "anonymous";
                ActorType actorType = ActorType.SYSTEM;

                if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof String email) {
                    actorId = email;
                    String role = auth.getAuthorities().stream().findFirst()
                            .map(a -> a.getAuthority().replace("ROLE_", ""))
                            .orElse("");
                    actorType = mapActor(role);
                }

                auditService.log(
                    actorType,
                    actorId,
                    request.getMethod(),
                    request.getRequestURI(),
                    "{\"status\":" + response.getStatus() + "}"
                );
            }
        } catch (Exception e) {
            log.debug("audit_trail persistence skipped: {}", e.getMessage());
        }
    }

    private boolean shouldAudit(HttpServletRequest req) {
        // Only audit mutating verbs.
        String method = req.getMethod();
        if (!"POST".equals(method) && !"PUT".equals(method)
                && !"PATCH".equals(method) && !"DELETE".equals(method)) {
            return false;
        }

        String uri = req.getRequestURI();
        if (uri == null) return false;

        if (uri.startsWith("/api/v1/notifications/mark-all-read")) return false;
        if (uri.endsWith("/audit-trails") && "POST".equals(method)) return false; // avoid recursion via the public POST
        return true;
    }

    private ActorType mapActor(String role) {
        if (role == null) return ActorType.SYSTEM;
        return switch (role) {
            case "CUSTOMER" -> ActorType.USER;
            case "TPP"      -> ActorType.TPP;

            default         -> ActorType.SYSTEM;
        };
    }
}
