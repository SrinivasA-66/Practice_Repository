package com.cts.openbankx.controller;

import com.cts.openbankx.model.ConsentEvent;
import com.cts.openbankx.repository.ConsentEventRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/consents")
public class ConsentEventController {

    private final ConsentEventRepository repo;

    public ConsentEventController(ConsentEventRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{consentId}/events")
    public List<ConsentEvent> getConsentEvents(@PathVariable Long consentId) {
        return repo.findByConsent_ConsentId(consentId);
    }
}