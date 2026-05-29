package com.cts.openbankx.controller;

import com.cts.openbankx.enums.PaymentStatus;
import com.cts.openbankx.model.PaymentInitiation;
import com.cts.openbankx.repository.PaymentInitiationRepository;
import com.cts.openbankx.service.PaymentExecutionService;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pisp")
public class PaymentInitiationController {

    private final PaymentInitiationRepository repo;
    private final PaymentExecutionService executionService;

    public PaymentInitiationController(
        PaymentInitiationRepository repo,
        PaymentExecutionService executionService
    ) {
        this.repo = repo;
        this.executionService = executionService;
    }

    @PostMapping("/payments")
    public PaymentInitiation initiate(@RequestBody PaymentInitiation p) {
        p.setStatus(PaymentStatus.CREATED);
        p.setCreatedDate(LocalDateTime.now());
        return repo.save(p);
    }

    @GetMapping("/payments")
    public List<PaymentInitiation> getAll() {
        return repo.findAll();
    }
    
    @PostMapping("/payments/{id}/execute")
    public PaymentInitiation execute(@PathVariable Long id) {
        return executionService.executePayment(id);
    }
}