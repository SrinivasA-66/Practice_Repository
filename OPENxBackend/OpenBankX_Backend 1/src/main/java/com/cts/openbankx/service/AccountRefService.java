package com.cts.openbankx.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.openbankx.enums.AccountStatus;
import com.cts.openbankx.enums.AccountType;
import com.cts.openbankx.enums.ConsentStatus;
import com.cts.openbankx.model.AccountRef;
import com.cts.openbankx.model.Consent;
import com.cts.openbankx.model.User;
import com.cts.openbankx.repository.AccountRefRepository;
import com.cts.openbankx.repository.UserRepository;

@Service
public class AccountRefService {

    private static final Logger log = LoggerFactory.getLogger(AccountRefService.class);

    private final AccountRefRepository repo;
    private final ConsentService consentService;
    private final UserRepository userRepository;

    public AccountRefService(AccountRefRepository repo,
                             ConsentService consentService,
                             UserRepository userRepository) {
        this.repo = repo;
        this.consentService = consentService;
        this.userRepository = userRepository;
    }


    @Bean
    public CommandLineRunner seedNewUserAccounts() {
        return args -> seedAccountsForNewUserInternal();
    }

    @Transactional
    public void seedAccountsForNewUserInternal() {
        try {

            java.util.Map<Long, User> byId = new java.util.LinkedHashMap<>();
            for (User u : userRepository.findByNameIgnoreCase("newuser")) {
                byId.put(u.getUserId(), u);
            }
            for (User u : userRepository.findByEmailContainingIgnoreCase("newuser")) {
                byId.put(u.getUserId(), u);
            }
            java.util.Collection<User> matches = byId.values();

            if (matches.isEmpty()) {
                log.warn("[seed] No user named 'newuser' (and no email containing 'newuser') found — register one and restart to seed accounts.");
                return;
            }
            for (User user : matches) {
                List<AccountRef> existing = repo.findByUser_UserId(user.getUserId());
                if (!existing.isEmpty()) {
                    log.info("[seed] User '{}' (#{}) already has {} account(s) — skipping.",
                            user.getName(), user.getUserId(), existing.size());
                    continue;
                }

                AccountRef savings = new AccountRef();
                savings.setUser(user);
                savings.setAccountNumberMasked("****-1234");
                savings.setType(AccountType.SAVINGS);
                savings.setCurrency("GBP");
                savings.setStatus(AccountStatus.ACTIVE);
                savings.setBalance(5000.00);
                repo.saveAndFlush(savings);

                AccountRef current = new AccountRef();
                current.setUser(user);
                current.setAccountNumberMasked("****-5678");
                current.setType(AccountType.CURRENT);
                current.setCurrency("GBP");
                current.setStatus(AccountStatus.ACTIVE);
                current.setBalance(2500.00);
                repo.saveAndFlush(current);

                log.info("[seed] Created 2 starter accounts (#{} SAVINGS, #{} CURRENT) for user '{}' (#{}).",
                        savings.getAccountId(), current.getAccountId(), user.getName(), user.getUserId());
            }
        } catch (Exception e) {
            log.error("[seed] Account seeding for 'newuser' failed: {}", e.getMessage(), e);
        }
    }



    public List<AccountRef> findByConsent(Long consentId) {
        Consent consent = consentService.findById(consentId);
        if (consent.getStatus() != ConsentStatus.ACTIVE) {
            throw new RuntimeException("Consent is not active");
        }
        return repo.findByUser_UserId(consent.getUser().getUserId());
    }



    public List<AccountRef> findAll() {
        return repo.findAll();
    }

    public AccountRef findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Account not found: " + id));
    }

    public AccountRef save(AccountRef a) {
        return repo.save(a);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<AccountRef> findByUser(Long userId) {
        return repo.findByUser_UserId(userId);
    }
}
