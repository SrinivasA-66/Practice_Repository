package com.cts.openbankx.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.openbankx.enums.ConsentStatus;
import com.cts.openbankx.enums.FundsCheckResult;
import com.cts.openbankx.enums.TxnType;
import com.cts.openbankx.model.Consent;
import com.cts.openbankx.model.FundsCheck;
import com.cts.openbankx.model.TransactionRef;
import com.cts.openbankx.repository.FundsCheckRepository;

@Service
public class FundsCheckService {
	private final FundsCheckRepository repo;
	private final ConsentService consentService;
	private final TransactionRefService txnService;

	public FundsCheckService(FundsCheckRepository repo, ConsentService consentService,
			TransactionRefService txnService) {
		this.repo = repo;
		this.consentService = consentService;
		this.txnService = txnService;
	}

	public FundsCheck performFundsCheck(FundsCheck f, Long consentId) {

		Consent consent = consentService.findById(consentId);
		if (consent.getStatus() != ConsentStatus.ACTIVE)
			throw new RuntimeException("Active consent required");


		Long accountId = f.getAccountRef().getAccountId();
		List<TransactionRef> history = txnService.findByAccountId(accountId);


		BigDecimal currentBalance = history.stream()
				.map(t -> t.getTxnType() == TxnType.CREDIT ? t.getAmount() : t.getAmount().negate())
				.reduce(BigDecimal.ZERO, BigDecimal::add);


		if (currentBalance.compareTo(f.getAmount()) >= 0) {
			f.setResult(FundsCheckResult.SUFFICIENT);
		} else {
			f.setResult(FundsCheckResult.INSUFFICIENT);
		}

		f.setCheckedDate(LocalDateTime.now());
		return repo.save(f);
	}


	public List<FundsCheck> findAll() {
		return repo.findAll();
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}
}