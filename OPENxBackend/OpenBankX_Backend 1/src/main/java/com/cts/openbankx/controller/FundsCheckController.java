package com.cts.openbankx.controller;

import com.cts.openbankx.enums.FundsCheckResult;
import com.cts.openbankx.model.AccountRef;
import com.cts.openbankx.model.FundsCheck;
import com.cts.openbankx.repository.AccountRefRepository;
import com.cts.openbankx.repository.FundsCheckRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/v1/cbpii")
public class FundsCheckController {

    private final FundsCheckRepository repo;
    private final AccountRefRepository accountRepo;

    public FundsCheckController(FundsCheckRepository repo, AccountRefRepository accountRepo) {
        this.repo = repo;
        this.accountRepo = accountRepo;
    }

    @PostMapping("/funds-check")
    public FundsCheck check(@RequestBody FundsCheck check) {

        if (check.getAccountRef() == null || check.getAccountRef().getAccountId() == null) {
            throw new IllegalArgumentException("accountRef.accountId is required.");
        }
        if (check.getAmount() == null) {
            throw new IllegalArgumentException("amount is required.");
        }
        if (check.getCurrency() == null || check.getCurrency().isBlank()) {
            check.setCurrency("GBP");
        }


        AccountRef account = accountRepo.findById(check.getAccountRef().getAccountId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Account not found: " + check.getAccountRef().getAccountId()));
        check.setAccountRef(account);

        BigDecimal balance = BigDecimal.valueOf(account.getBalance());
        boolean sufficient = balance.compareTo(check.getAmount()) >= 0;

        check.setResult(sufficient ? FundsCheckResult.SUFFICIENT : FundsCheckResult.INSUFFICIENT);
        check.setCheckedDate(LocalDateTime.now());
        return repo.save(check);
    }
}
