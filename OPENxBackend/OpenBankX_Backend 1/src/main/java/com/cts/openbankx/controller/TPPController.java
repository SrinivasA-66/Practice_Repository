package com.cts.openbankx.controller;

import com.cts.openbankx.model.TPP;
import com.cts.openbankx.service.TPPService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tpps")
public class TPPController {

    private final TPPService service;

    public TPPController(TPPService service) {
    	this.service = service;
    }

    @PostMapping
    public TPP register(@RequestBody TPP tpp) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String email && !email.isBlank()) {
            tpp.setOwnerEmail(email);
        }
        return service.register(tpp);
    }

    @GetMapping
    public List<TPP> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}/status")
    public TPP updateStatus(@PathVariable Long id, @RequestParam String status) {
        TPP tpp = service.getAll().stream()
            .filter(t -> t.getTppId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("TPP not found"));
        tpp.setStatus(com.cts.openbankx.enums.TPPStatus.valueOf(status));
        return service.register(tpp);
    }
}