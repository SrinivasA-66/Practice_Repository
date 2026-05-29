package com.cts.openbankx.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.openbankx.model.TransactionRef;
import com.cts.openbankx.service.TransactionRefService;

@RestController
@RequestMapping("/api/v1/aisp/accounts")
public class TransactionRefController {

    private final TransactionRefService service;

    public TransactionRefController(TransactionRefService service) {
        this.service = service;
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionRef>> getTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(service.findByAccountId(id));
    }

    @PostMapping("/{id}/transactions")
    public ResponseEntity<TransactionRef> createTransaction(
            @PathVariable Long id,
            @RequestBody TransactionRef txn) {
        txn.getAccountRef().setAccountId(id);
        if (txn.getTxnDate() == null) {
            txn.setTxnDate(LocalDateTime.now());
        }
        TransactionRef saved = service.save(txn);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/transactions/all")
    public ResponseEntity<List<TransactionRef>> getAllTransactions() {
        return ResponseEntity.ok(service.findAll());
    }
}