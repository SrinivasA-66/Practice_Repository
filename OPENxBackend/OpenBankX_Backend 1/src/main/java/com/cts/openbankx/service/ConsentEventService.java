package com.cts.openbankx.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.openbankx.model.ConsentEvent;
import com.cts.openbankx.repository.ConsentEventRepository;

@Service
public class ConsentEventService {
	private final ConsentEventRepository repo;

	public ConsentEventService(ConsentEventRepository repo) {
		this.repo = repo;
	}

	public List<ConsentEvent> findAll() {
		return repo.findAll();
	}

	

	public ConsentEvent save(ConsentEvent e) {
		return repo.save(e);
	}
}
