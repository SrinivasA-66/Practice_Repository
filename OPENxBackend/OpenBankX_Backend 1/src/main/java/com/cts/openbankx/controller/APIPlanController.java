package com.cts.openbankx.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.openbankx.model.APIPlan;
import com.cts.openbankx.service.APIPlanService;

@RestController
@RequestMapping("/api/v1/api-plans")
public class APIPlanController {

    private final APIPlanService apiPlanService;

    public APIPlanController(APIPlanService apiPlanService) {
        this.apiPlanService = apiPlanService;
    }


    @GetMapping
    public ResponseEntity<List<APIPlan>> getAllPlans() {
        return ResponseEntity.ok(apiPlanService.findAll());
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<List<APIPlan>> getByProduct(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
            apiPlanService.findByProduct(productId)
        );
    }


    @PostMapping
    public ResponseEntity<APIPlan> createPlan(
            @RequestBody APIPlan plan) {

        APIPlan saved = apiPlanService.save(plan);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    @PutMapping("/{id}")
    public ResponseEntity<APIPlan> updatePlan(
            @PathVariable Long id,
            @RequestBody APIPlan plan) {

        plan.setPlanId(id);
        return ResponseEntity.ok(apiPlanService.save(plan));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        apiPlanService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}