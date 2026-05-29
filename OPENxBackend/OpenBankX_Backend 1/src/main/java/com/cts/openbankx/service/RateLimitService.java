package com.cts.openbankx.service;

import com.cts.openbankx.model.RateLimitCounter;
import com.cts.openbankx.repository.RateLimitCounterRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RateLimitService {
    private final RateLimitCounterRepository repo;

    public RateLimitService(RateLimitCounterRepository repo) {
        this.repo = repo;
    }

    public List<RateLimitCounter> findAll() { return repo.findAll(); }
    public RateLimitCounter save(RateLimitCounter c) { return repo.save(c); }
    public List<RateLimitCounter> findByTppApp(Long tppAppId) { return repo.findByTppApp_TppAppId(tppAppId); }
}
