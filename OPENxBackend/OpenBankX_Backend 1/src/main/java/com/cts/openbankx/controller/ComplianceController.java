package com.cts.openbankx.controller;

import com.cts.openbankx.model.ComplianceReport;
import com.cts.openbankx.service.ComplianceReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compliance")
public class ComplianceController {

    private final ComplianceReportService service;
    
    public ComplianceController(ComplianceReportService service) {
    	this.service = service;
    }

    @PostMapping("/reports")
    public ComplianceReport generate(
            @RequestParam String scope,
            @RequestParam String metrics) {
        return service.generate(scope, metrics);
    }

    @GetMapping("/reports")
    public List<ComplianceReport> getAll() {
        return service.getAll();
    }
}