package com.cts.openbankx.controller;

import com.cts.openbankx.model.AuthClient;
import com.cts.openbankx.service.AuthClientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth-clients")
public class AuthClientController {

    private final AuthClientService service;
    public AuthClientController(AuthClientService service) {
    	this.service = service;
    }

    @PostMapping
    public AuthClient create(@RequestBody AuthClient client) {
        return service.create(client);
    }

    @GetMapping
    public List<AuthClient> getAll() {
        return service.findAll();
    }
}