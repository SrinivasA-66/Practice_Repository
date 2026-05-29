package com.cts.openbankx.controller;

import com.cts.openbankx.model.SCAEvent;
import com.cts.openbankx.model.User;
import com.cts.openbankx.repository.UserRepository;
import com.cts.openbankx.service.SCAEventService;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sca")
public class SCAEventController {

    private final SCAEventService service;
    private final UserRepository userRepo;

    public SCAEventController(SCAEventService service, UserRepository userRepo) {
        this.service = service;
        this.userRepo = userRepo;
    }

    @PostMapping("/otp")
    public String generateOtp(
            @RequestParam Long userId,
            @RequestParam String referenceId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return service.generateOtp(user, referenceId);
    }

    @GetMapping("/events")
    public List<SCAEvent> getScaEvents(
            @RequestParam(required = false) String referenceId) {

        if (referenceId != null && !referenceId.isEmpty()) {
            return service.findByReferenceId(referenceId);
        }
        return service.findAll();
    }
}