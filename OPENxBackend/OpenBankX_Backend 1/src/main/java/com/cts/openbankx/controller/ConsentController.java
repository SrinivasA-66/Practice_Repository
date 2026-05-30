package com.cts.openbankx.controller;

import com.cts.openbankx.model.Consent;
import com.cts.openbankx.service.ConsentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/consents")
public class ConsentController {

    private final ConsentService service;

    public ConsentController(ConsentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Consent> getAll() {
        return service.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Consent> getByUser(@PathVariable Long userId) {
        return service.findByUserId(userId);
    }

    @PostMapping
    public Consent create(@RequestBody Consent consent) {
        return service.create(consent);
    }

    @GetMapping("/{id}")
    public Consent getConsent(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}/revoke")
    public Consent revoke(@PathVariable Long id) {
        return service.revoke(id);
    }


    @PutMapping("/{id}/activate")
    public Consent activate(@PathVariable Long id,
                            @RequestBody(required = false) java.util.Map<String, String> body) {
        com.cts.openbankx.enums.SCAMethod method = com.cts.openbankx.enums.SCAMethod.OTP;
        if (body != null && body.get("method") != null) {
            try {
                method = com.cts.openbankx.enums.SCAMethod.valueOf(body.get("method"));
            } catch (IllegalArgumentException ignored) { /* keep default */ }
        }
        return service.activateAfterSca(id, method);
    }


    @PutMapping("/{id}/scopes")
    public Consent updateScopes(@PathVariable Long id,
                                @RequestBody java.util.Map<String, String> body) {
        return service.updateScopes(id, body.get("scopeJSON"));
    }
}