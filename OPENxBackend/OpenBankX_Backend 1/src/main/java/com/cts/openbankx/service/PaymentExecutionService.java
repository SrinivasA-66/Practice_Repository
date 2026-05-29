package com.cts.openbankx.service;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cts.openbankx.enums.PaymentStatus;
import com.cts.openbankx.enums.TxnType;
import com.cts.openbankx.model.*;
import com.cts.openbankx.repository.*;

@Service
@Transactional
public class PaymentExecutionService {

    private static final Logger log = LoggerFactory.getLogger(PaymentExecutionService.class);

    private final PaymentInitiationRepository paymentRepo;
    private final AccountRefRepository accountRepo;
    private final TransactionRefRepository txnRepo;


    @Autowired
    @Lazy
    private PaymentExecutionService self;

    public PaymentExecutionService(
        PaymentInitiationRepository paymentRepo,
        AccountRefRepository accountRepo,
        TransactionRefRepository txnRepo
    ) {
        this.paymentRepo = paymentRepo;
        this.accountRepo = accountRepo;
        this.txnRepo = txnRepo;
    }

    public PaymentInitiation executePayment(Long paymentId) {

        PaymentInitiation p = paymentRepo.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (p.getStatus() != PaymentStatus.CREATED) {
            throw new RuntimeException("Payment already processed");
        }

        AccountRef debtor = accountRepo.findById(
            Long.parseLong(p.getDebtorAccountRef())
        ).orElseThrow(() -> new RuntimeException("Debtor account not found"));



BigDecimal debtorBalance = BigDecimal.valueOf(debtor.getBalance());

if (debtorBalance.compareTo(p.getAmount()) < 0) {
    p.setStatus(PaymentStatus.REJECTED);
    return paymentRepo.save(p);
}




debtor.setBalance(
 debtorBalance.subtract(p.getAmount()).doubleValue()
);
accountRepo.save(debtor);

        TransactionRef debitTxn = new TransactionRef();
        debitTxn.setAccountRef(debtor);
        debitTxn.setTxnType(TxnType.DEBIT);
        debitTxn.setAmount(p.getAmount());
        debitTxn.setNarrative("Payment sent");
        debitTxn.setTxnDate(LocalDateTime.now());
        txnRepo.save(debitTxn);


        String creditorRef = p.getCreditorAccountRef();
        try {

            self.applyCredit(creditorRef, p.getAmount());
        } catch (Exception e) {
            log.error("Credit branch failed for ref '{}': {}. Debit still preserved.",
                creditorRef, e.getMessage(), e);
        }

        // ✅ Final status
        p.setStatus(PaymentStatus.EXECUTED);
        return paymentRepo.save(p);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void applyCredit(String creditorRef, BigDecimal amount) {
        Optional<AccountRef> creditorOpt = findInternalCreditor(creditorRef);
        if (creditorOpt.isEmpty()) {
            log.warn("Credit SKIPPED: no internal account matched creditor ref '{}' " +
                     "— payment treated as outbound. Check account_ref.account_number_masked.",
                     creditorRef);
            return;
        }
        AccountRef creditor = creditorOpt.get();


        double existingBalance =
            creditor.getBalance() == null ? 0.0 : creditor.getBalance();
        BigDecimal newBalance = BigDecimal.valueOf(existingBalance).add(amount);

        creditor.setBalance(newBalance.doubleValue());
        accountRepo.saveAndFlush(creditor);

        TransactionRef creditTxn = new TransactionRef();
        creditTxn.setAccountRef(creditor);
        creditTxn.setTxnType(TxnType.CREDIT);
        creditTxn.setAmount(amount);
        creditTxn.setNarrative("Incoming payment");
        creditTxn.setTxnDate(LocalDateTime.now());
        txnRepo.saveAndFlush(creditTxn);

        log.info("Credit applied: account_id={} mask='{}' amount={} new_balance={}",
            creditor.getAccountId(), creditor.getAccountNumberMasked(),
            amount, creditor.getBalance());
    }


    private Optional<AccountRef> findInternalCreditor(String rawRef) {
        if (rawRef == null) return Optional.empty();
        String trimmed = rawRef.trim();
        if (trimmed.isEmpty()) return Optional.empty();

        List<AccountRef> all;
        try {
            all = accountRepo.findAll();
        } catch (Exception e) {
            log.error("findAll() failed during creditor lookup: {}", e.getMessage(), e);
            return Optional.empty();
        }


        for (AccountRef a : all) {
            if (trimmed.equals(a.getAccountNumberMasked())) {
                log.info("Creditor matched by EXACT mask '{}' -> account_id={}",
                    trimmed, a.getAccountId());
                return Optional.of(a);
            }
        }

        String typedDigits = trimmed.replaceAll("[^0-9]", "");
        if (typedDigits.isEmpty()) {
            log.info("Creditor '{}' has no digits — nothing to match.", trimmed);
            return Optional.empty();
        }

        if (typedDigits.length() <= 19) {
            try {
                long maybeId = Long.parseLong(typedDigits);
                for (AccountRef a : all) {
                    if (a.getAccountId() != null && a.getAccountId() == maybeId) {
                        log.info("Creditor matched by ACCOUNT_ID '{}' -> mask='{}'",
                            maybeId, a.getAccountNumberMasked());
                        return Optional.of(a);
                    }
                }
            } catch (NumberFormatException ignored) { }
        }


        if (typedDigits.length() < 4) {
            log.info("Creditor '{}' has fewer than 4 digits ({}); cannot match by last-4.",
                trimmed, typedDigits);
            return Optional.empty();
        }
        String typedLast4 = typedDigits.substring(typedDigits.length() - 4);

        for (AccountRef a : all) {
            String mask = a.getAccountNumberMasked();
            if (mask == null) continue;
            String storedDigits = mask.replaceAll("[^0-9]", "");
            if (storedDigits.length() < 4) continue;
            if (storedDigits.substring(storedDigits.length() - 4).equals(typedLast4)) {
                log.info("Creditor matched by LAST 4 DIGITS '{}' -> account_id={} mask='{}'",
                    typedLast4, a.getAccountId(), a.getAccountNumberMasked());
                return Optional.of(a);
            }
        }

        log.warn("Creditor LAST 4 DIGITS '{}' did not match any account_ref row " +
                 "(checked {} accounts). Will be treated as outbound.",
                 typedLast4, all.size());
        return Optional.empty();
    }
}