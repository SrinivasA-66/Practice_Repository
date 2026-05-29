package com.cts.openbankx.controller;

import com.cts.openbankx.model.TPPSubscription;
import com.cts.openbankx.service.TPPSubscriptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final TPPSubscriptionService service;

    public SubscriptionController(TPPSubscriptionService service) {
        this.service = service;
    }

    @GetMapping
    public List<TPPSubscription> getAll() {
        return service.findAll();
    }

    @GetMapping("/app/{tppAppId}")
    public List<TPPSubscription> getByApp(@PathVariable Long tppAppId) {
        return service.findByTppApp(tppAppId);
    }

    @PostMapping
    public TPPSubscription create(@RequestBody TPPSubscription sub) {
        return service.save(sub);
    }


    @PutMapping("/{id}/cancel")
    public TPPSubscription cancel(@PathVariable Long id) {
        return service.cancel(id);
    }
}