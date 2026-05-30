package com.cts.openbankx.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cts.openbankx.model.APILog;
import com.cts.openbankx.repository.APILogRepository;

@Service
public class APILogService {

    private final APILogRepository repo;

    public APILogService(APILogRepository repo) {
        this.repo = repo;
    }

    public APILog save(APILog log) {
        return repo.save(log);
    }

    public List<APILog> findAll() {
        return repo.findAll();
    }

    public APILog findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("APILog not found"));
    }

    public List<APILog> findByTppApp(Long appId) {
        return repo.findByTppApp_TppAppId(appId);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }


    public List<APILog> findErrorLogs() {
        return repo.findAll()
                .stream()
                .filter(log -> log.getStatusCode() != null
                        && log.getStatusCode() >= 400)
                .collect(Collectors.toList());
    }


    public List<APILog> findHealthyLogs() {
        return repo.findAll()
                .stream()
                .filter(log -> log.getStatusCode() != null
                        && log.getStatusCode() < 400)
                .collect(Collectors.toList());
    }


    public List<APILog> findByMethod(String method) {
        return repo.findAll()
                .stream()
                .filter(log -> method.equalsIgnoreCase(log.getMethod()))
                .collect(Collectors.toList());
    }


    public APILog updateLog(Long id, Integer newStatusCode, Integer newLatencyMs) {
        APILog log = findById(id);
        if (newStatusCode != null) log.setStatusCode(newStatusCode);
        if (newLatencyMs  != null) log.setLatencyMs(newLatencyMs);
        return repo.save(log);
    }


    public APILog patchStatus(Long id, Integer newStatusCode) {
        APILog log = findById(id);
        if (newStatusCode != null) log.setStatusCode(newStatusCode);
        return repo.save(log);
    }
}