package com.cts.openbankx.service;

import com.cts.openbankx.enums.*;
import com.cts.openbankx.model.*;
import com.cts.openbankx.repository.SCAEventRepository;
//import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service

public class SCAEventService {

    private final SCAEventRepository repo;
    
    public SCAEventService(SCAEventRepository repo) {
    	this.repo=repo;
    }

    public String generateOtp(User user, String referenceId) {

        String otp = String.format("%06d",
                new Random().nextInt(1_000_000));

        SCAEvent event = new SCAEvent();
        event.setUser(user);
        event.setMethod(SCAMethod.OTP);
        event.setResult(SCAResult.PASS);
        event.setEventTime(LocalDateTime.now());
        event.setReferenceId(referenceId);

        repo.save(event);


        return otp;
    }

    public List<SCAEvent> findByReferenceId(String referenceId) {
        return repo.findByReferenceId(referenceId);
    }

    public List<SCAEvent> findAll() {
        return repo.findAll();
    }
}