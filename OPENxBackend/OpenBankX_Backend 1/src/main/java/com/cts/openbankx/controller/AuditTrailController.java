package com.cts.openbankx.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.openbankx.enums.ActorType;
import com.cts.openbankx.model.AuditTrail;
import com.cts.openbankx.service.AuditTrailService;

@RestController
@RequestMapping({ "/api/v1/audit-trails", "/api/audit-trails" })
public class AuditTrailController {

    private final AuditTrailService auditTrailService;

    public AuditTrailController(AuditTrailService auditTrailService) {
        this.auditTrailService = auditTrailService;
    }

    @GetMapping
    public ResponseEntity<List<AuditTrail>> getAllAuditTrails() {
        return ResponseEntity.ok(auditTrailService.findAll());
    }

    @PostMapping
    public ResponseEntity<AuditTrail> createAuditTrail(@RequestBody AuditTrail auditTrail) {
        AuditTrail saved = auditTrailService.save(auditTrail);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/log")
    public ResponseEntity<AuditTrail> logAction(
            @RequestParam ActorType actorType,
            @RequestParam String actorId,
            @RequestParam String action,
            @RequestParam String resource,
            @RequestParam(required = false) String metadata) {

        AuditTrail audit = auditTrailService.log(actorType, actorId, action, resource, metadata);
        return new ResponseEntity<>(audit, HttpStatus.CREATED);
    }

    @GetMapping("/actor/{actorId}")
    public ResponseEntity<List<AuditTrail>> getByActor(@PathVariable String actorId) {
        return ResponseEntity.ok(auditTrailService.findByActor(actorId));
    }

    @GetMapping("/resource/{resource}")
    public ResponseEntity<List<AuditTrail>> getByResource(@PathVariable String resource) {
        return ResponseEntity.ok(auditTrailService.findByResource(resource));
    }
}