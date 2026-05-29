package com.cts.openbankx.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.openbankx.enums.*;
import com.cts.openbankx.model.*;
import com.cts.openbankx.repository.*;

@Service
public class ConsentService {

    private final ConsentRepository consentRepo;
    private final ConsentEventRepository eventRepo;
    private final SCAEventRepository scaEventRepo;
    private final NotificationService notificationService;

    public ConsentService(
            ConsentRepository consentRepo,
            ConsentEventRepository eventRepo,
            SCAEventRepository scaEventRepo,
            NotificationService notificationService) {

        this.consentRepo = consentRepo;
        this.eventRepo = eventRepo;
        this.scaEventRepo = scaEventRepo;
        this.notificationService = notificationService;
    }

    public List<Consent> findAll() {
        return consentRepo.findAll();
    }

    public List<Consent> findByUserId(Long userId) {
        return consentRepo.findByUser_UserId(userId);
    }

    public Consent create(Consent consent) {

        LocalDateTime now = LocalDateTime.now();

        consent.setCreatedDate(now);
        consent.setExpiryDate(now.plusDays(90));
        consent.setStatus(ConsentStatus.AWAITING_SCA);

        Consent saved = consentRepo.save(consent);

        logEvent(
            saved,
            ConsentEventType.CREATE,
            PerformedBy.USER,
            "Consent created — awaiting SCA verification"
        );

        notificationService.notifyUser(
            saved.getUser().getUserId(),
            "Consent created — valid until " + saved.getExpiryDate(),
            NotificationCategory.CONSENT
        );

        return saved;
    }

    public Consent activateAfterSca(Long id) {
        return activateAfterSca(id, SCAMethod.OTP);
    }


    public Consent activateAfterSca(Long id, SCAMethod method) {

        Consent consent = consentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Consent not found"));

        if (consent.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Consent expired");
        }

        consent.setStatus(ConsentStatus.ACTIVE);
        Consent saved = consentRepo.save(consent);


        SCAEvent sca = new SCAEvent();
        sca.setUser(saved.getUser());
        sca.setMethod(method != null ? method : SCAMethod.OTP);
        sca.setResult(SCAResult.PASS);
        sca.setEventTime(LocalDateTime.now());
        sca.setReferenceId(String.valueOf(saved.getConsentId()));
        scaEventRepo.save(sca);

        logEvent(
            saved,
            ConsentEventType.AMEND,
            PerformedBy.USER,
            "SCA verified (" + sca.getMethod() + ") — consent activated"
        );

        return saved;
    }

    public Consent updateScopes(Long id, String newScopeJson) {
        Consent consent = consentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Consent not found: " + id));

        if (consent.getStatus() != ConsentStatus.ACTIVE
                && consent.getStatus() != ConsentStatus.AWAITING_SCA) {
            throw new IllegalArgumentException(
                "Only ACTIVE or AWAITING_SCA consents can be amended.");
        }
        if (newScopeJson == null || newScopeJson.isBlank() || newScopeJson.trim().equals("[]")) {

            consent.setStatus(ConsentStatus.REVOKED);
            Consent saved = consentRepo.save(consent);
            logEvent(saved, ConsentEventType.REVOKE, PerformedBy.USER,
                     "All permissions withdrawn by user");
            return saved;
        }

        consent.setScopeJSON(newScopeJson);
        Consent saved = consentRepo.save(consent);

        logEvent(saved, ConsentEventType.AMEND, PerformedBy.USER,
                 "Scopes updated by user");

        notificationService.notifyUser(
            saved.getUser().getUserId(),
            "Consent #" + saved.getConsentId() + " permissions updated.",
            NotificationCategory.CONSENT
        );

        return saved;
    }

    public Consent revoke(Long id) {
        Consent consent = consentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Consent not found"));

        consent.setStatus(ConsentStatus.REVOKED);
        Consent saved = consentRepo.save(consent);

        logEvent(saved, ConsentEventType.REVOKE, PerformedBy.USER, "Consent revoked by user");
        return saved;
    }

    public Consent findById(Long id) {
        return consentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Consent not found"));
    }

    private void logEvent(Consent consent, ConsentEventType type, PerformedBy by, String notes) {
        ConsentEvent ev = new ConsentEvent();
        ev.setConsent(consent);
        ev.setEventType(type);
        ev.setEventDate(LocalDateTime.now());
        ev.setPerformedBy(by);
        ev.setNotes(notes);
        eventRepo.save(ev);
    }
}
