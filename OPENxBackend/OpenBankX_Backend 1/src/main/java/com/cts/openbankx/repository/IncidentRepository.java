package com.cts.openbankx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.openbankx.enums.IncidentStatus;
import com.cts.openbankx.model.Incident;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
	List<Incident> findByStatus(IncidentStatus status);
}