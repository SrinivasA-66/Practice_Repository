package com.cts.openbankx.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.openbankx.model.TransactionRef;
import com.cts.openbankx.repository.TransactionRefRepository;

@Service
public class TransactionRefService {
	private final TransactionRefRepository repo;

	public TransactionRefService(TransactionRefRepository repo) {
		this.repo = repo;
	}


	public List<TransactionRef> findByAccountId(Long accountId) {
		return repo.findByAccountRef_AccountId(accountId);
	}


	public List<TransactionRef> findAll() {
		return repo.findAll();
	}

	public TransactionRef findById(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Txn not found"));
	}

	public TransactionRef save(TransactionRef t) {
		return repo.save(t);
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}
}