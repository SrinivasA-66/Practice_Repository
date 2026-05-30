package com.cts.openbankx.controller;

import com.cts.openbankx.model.AccountRef;
import com.cts.openbankx.model.TransactionRef;
import com.cts.openbankx.service.AccountRefService;
import com.cts.openbankx.repository.TransactionRefRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/aisp")
public class AccountRefController {

    private final AccountRefService accountService;
    private final TransactionRefRepository txnRepo;

    public AccountRefController(AccountRefService accountService,
                                TransactionRefRepository txnRepo) {
        this.accountService = accountService;
        this.txnRepo = txnRepo;
    }


    @GetMapping("/accounts/user/{userId}")
    public List<AccountRef> getAccountsByUser(
            @PathVariable Long userId) {
        return accountService.findByUser(userId);
    }


    @GetMapping("/accounts/consent/{consentId}")
    public List<AccountRef> getAccountsByConsent(
            @PathVariable Long consentId) {
        return accountService.findByConsent(consentId);
    }
}