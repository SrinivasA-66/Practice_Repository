package com.cts.openbankx.controller;

import com.cts.openbankx.enums.TokenType;
import com.cts.openbankx.enums.UserStatus;
import com.cts.openbankx.model.User;
import com.cts.openbankx.repository.UserRepository;
import com.cts.openbankx.config.JwtUtil;
import com.cts.openbankx.service.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("An account with this email already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        User saved = userRepository.save(user);


        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRole().name());
        try {
            tokenService.createAndSave(
                saved.getEmail(),
                saved.getRole().name(),
                token,
                "POST /api/v1/auth/register",
                TokenType.ACCESS
            );
        } catch (Exception ignored) {

        }

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("userId", saved.getUserId());
        response.put("name", saved.getName());
        response.put("email", saved.getEmail());
        response.put("role", saved.getRole().name());
        return response;
    }


    @GetMapping("/me")
    public Map<String, Object> me(@org.springframework.security.core.annotation.AuthenticationPrincipal Object principal) {
        if (!(principal instanceof String email)) {
            throw new RuntimeException("Not authenticated");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        try {
            tokenService.createAndSave(user.getEmail(), user.getRole().name(),
                    token, "GET /api/v1/auth/me", TokenType.ACCESS);
        } catch (Exception ignored) {}

        Map<String, Object> body = new HashMap<>();
        body.put("userId", user.getUserId());
        body.put("name", user.getName());
        body.put("email", user.getEmail());
        body.put("role", user.getRole().name());
        body.put("accessToken", token);
        return body;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {

        User user = userRepository.findByEmail(request.get("email"))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.get("password"), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }


        if (user.getStatus() == UserStatus.LOCKED) {
            throw new RuntimeException("User account is locked");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        try {
            tokenService.createAndSave(
                user.getEmail(),
                user.getRole().name(),
                token,
                "POST /api/v1/auth/login",
                TokenType.ACCESS
            );
        } catch (Exception e) {

        }

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("userId", user.getUserId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());

        return response;
    }
}