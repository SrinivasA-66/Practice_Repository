package com.cts.openbankx.service;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.openbankx.enums.UserStatus;
import com.cts.openbankx.model.User;
import com.cts.openbankx.repository.AccountRefRepository;
import com.cts.openbankx.repository.ConsentEventRepository;
import com.cts.openbankx.repository.ConsentRepository;
import com.cts.openbankx.repository.FundsCheckRepository;
import com.cts.openbankx.repository.SCAEventRepository;
import com.cts.openbankx.repository.TokenRepository;
import com.cts.openbankx.repository.TransactionRefRepository;
import com.cts.openbankx.repository.UserRepository;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    private final TokenRepository tokenRepo;
    private final ConsentRepository consentRepo;
    private final ConsentEventRepository consentEventRepo;
    private final SCAEventRepository scaEventRepo;
    private final AccountRefRepository accountRepo;
    private final FundsCheckRepository fundsCheckRepo;
    private final TransactionRefRepository transactionRepo;

    public UserService(UserRepository repo,
                       PasswordEncoder passwordEncoder,
                       TokenRepository tokenRepo,
                       ConsentRepository consentRepo,
                       ConsentEventRepository consentEventRepo,
                       SCAEventRepository scaEventRepo,
                       AccountRefRepository accountRepo,
                       FundsCheckRepository fundsCheckRepo,
                       TransactionRefRepository transactionRepo) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepo = tokenRepo;
        this.consentRepo = consentRepo;
        this.consentEventRepo = consentEventRepo;
        this.scaEventRepo = scaEventRepo;
        this.accountRepo = accountRepo;
        this.fundsCheckRepo = fundsCheckRepo;
        this.transactionRepo = transactionRepo;
    }

    public List<User> findAll() {
        return repo.findAll();
    }

    public User findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }
        repo.findByEmail(user.getEmail()).ifPresent(existing -> {
            throw new IllegalArgumentException("A user with email '" + user.getEmail() + "' already exists.");
        });
        if (user.getStatus() == null) user.setStatus(UserStatus.ACTIVE);
        return save(user);



    }

    public User save(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return repo.save(user);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }

        int fundsCheckRows = fundsCheckRepo.deleteAllByAccountUserId(id);
        int txnRows        = transactionRepo.deleteAllByAccountUserId(id);
        int accountRows    = accountRepo.deleteAllByUserId(id);

        int consentEventRows = consentEventRepo.deleteAllByConsentUserId(id);
        int consentRows      = consentRepo.deleteAllByUserId(id);

        int scaRows   = scaEventRepo.deleteAllByUserId(id);
        int tokenRows = tokenRepo.deleteAllByUserId(id);

        repo.deleteById(id);

        log.info("Deleted user {} along with {} funds-check, {} txn-ref, {} account, " +
                 "{} consent-event, {} consent, {} sca-event, {} token rows.",
                 id, fundsCheckRows, txnRows, accountRows,
                 consentEventRows, consentRows, scaRows, tokenRows);
    }
}
