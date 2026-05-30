package com.cts.openbankx.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.openbankx.enums.ActorType;
import com.cts.openbankx.model.AuditTrail;
import com.cts.openbankx.repository.AuditTrailRepository;

@Service
public class AuditTrailService {
	private final AuditTrailRepository auditRepo;

	public AuditTrailService(AuditTrailRepository auditRepo) {
		this.auditRepo = auditRepo;
	}

	public List<AuditTrail> findAll() {
		return auditRepo.findAll();
	}

	public AuditTrail log(ActorType actorType, String actorId, String action, String resource, String metadata) {
		AuditTrail trail = new AuditTrail();
		trail.setActorType(actorType);
		trail.setActorId(actorId);
		trail.setAction(action);
		trail.setResource(resource);
		trail.setTimestamp(LocalDateTime.now());
		trail.setMetadata(metadata);
		return auditRepo.save(trail);
	}

	public AuditTrail save(AuditTrail a) {
		if (a.getTimestamp() == null)
			a.setTimestamp(LocalDateTime.now());
		return auditRepo.save(a);
	}

	public List<AuditTrail> findByActor(String actorId) {
		return auditRepo.findByActorId(actorId);
	}

	public List<AuditTrail> findByResource(String resource) {
		return auditRepo.findByResource(resource);
	}
}
