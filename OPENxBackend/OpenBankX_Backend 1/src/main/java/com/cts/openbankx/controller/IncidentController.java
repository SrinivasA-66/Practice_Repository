package com.cts.openbankx.controller;

import com.cts.openbankx.enums.IncidentStatus;
import com.cts.openbankx.model.Incident;
import com.cts.openbankx.repository.IncidentRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentRepository repo;

    public IncidentController(IncidentRepository repo) {
    	this.repo = repo;
    }

    @GetMapping
    public List<Incident> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public Incident create(@RequestBody Incident i) {
        i.setDetectedDate(LocalDateTime.now());
        i.setStatus(IncidentStatus.OPEN);
        return repo.save(i);
    }

    @PutMapping("/{id}/close")
    public Incident close(@PathVariable Long id) {
        Incident i = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found"));
        i.setStatus(IncidentStatus.CLOSED);
        return repo.save(i);
    }
}