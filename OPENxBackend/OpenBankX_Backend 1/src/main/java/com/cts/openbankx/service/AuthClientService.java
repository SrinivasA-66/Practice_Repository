package com.cts.openbankx.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.openbankx.model.AuthClient;
import com.cts.openbankx.repository.AuthClientRepository;

@Service
public class AuthClientService {
	private final AuthClientRepository repo;

	public AuthClientService(AuthClientRepository repo) {
		this.repo = repo;
	}


public AuthClient create(AuthClient client) {
        client.setStatus("ACTIVE");
        return repo.save(client);
    }

    public List<AuthClient> findAll() {
        return repo.findAll();
    }
}
