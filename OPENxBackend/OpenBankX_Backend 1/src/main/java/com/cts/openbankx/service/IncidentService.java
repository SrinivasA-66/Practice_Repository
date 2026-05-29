package com.cts.openbankx.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.openbankx.enums.IncidentStatus;
import com.cts.openbankx.model.Incident;
import com.cts.openbankx.repository.IncidentRepository;

@Service
public class IncidentService {
	private final IncidentRepository incidentRepo;

	public IncidentService(IncidentRepository incidentRepo) {
		this.incidentRepo = incidentRepo;
	}

	public List<Incident> findAll() {
		return incidentRepo.findAll();
	}

	public Incident findById(Long id) {
		return incidentRepo.findById(id).orElseThrow(() -> new RuntimeException("Incident not found: " + id));
	}

	public Incident save(Incident i) {
		if (i.getDetectedDate() == null)
			i.setDetectedDate(LocalDateTime.now());
		return incidentRepo.save(i);
	}

	public Incident mitigate(Long id) {
		Incident i = findById(id);
		i.setStatus(IncidentStatus.MITIGATED);
		return incidentRepo.save(i);
	}

	public Incident close(Long id) {
		Incident i = findById(id);
		i.setStatus(IncidentStatus.CLOSED);
		return incidentRepo.save(i);
	}

	public List<Incident> findByStatus(IncidentStatus status) {
		return incidentRepo.findByStatus(status);
	}
}
