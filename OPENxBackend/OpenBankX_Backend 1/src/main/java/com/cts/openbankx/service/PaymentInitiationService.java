package com.cts.openbankx.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.openbankx.enums.PaymentStatus;
import com.cts.openbankx.model.PaymentInitiation;
import com.cts.openbankx.repository.PaymentInitiationRepository;

@Service
public class PaymentInitiationService {
	private final PaymentInitiationRepository repo;

	public PaymentInitiationService(PaymentInitiationRepository repo) {
		this.repo = repo;
	}


	public List<PaymentInitiation> findAll() {
		return repo.findAll();
	}

	public PaymentInitiation findById(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
	}

	public PaymentInitiation initiate(PaymentInitiation p) {
		p.setStatus(PaymentStatus.CREATED);
		p.setCreatedDate(LocalDateTime.now());
		return repo.save(p);
	}

	public PaymentInitiation updateStatus(Long id, PaymentStatus status) {
		PaymentInitiation p = findById(id);
		p.setStatus(status);
		return repo.save(p);
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}
}