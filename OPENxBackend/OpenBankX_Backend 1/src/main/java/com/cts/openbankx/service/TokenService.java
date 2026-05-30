package com.cts.openbankx.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cts.openbankx.enums.TokenStatus;
import com.cts.openbankx.enums.TokenType;
import com.cts.openbankx.model.Token;
import com.cts.openbankx.model.User;
import com.cts.openbankx.repository.TokenRepository;
import com.cts.openbankx.repository.UserRepository;

@Service
public class TokenService {
	private final TokenRepository repo;
	private final UserRepository userRepo;

	@Value("${app.jwt.expiration-ms}")
	private long expirationMs;

	public TokenService(TokenRepository repo, UserRepository userRepo) {
		this.repo = repo;
		this.userRepo = userRepo;
	}

	public List<Token> findAll() {
		return repo.findAll();
	}

	public Token findById(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Token not found: " + id));
	}

	public Token save(Token t) {
		return repo.save(t);
	}

	public List<Token> findByUser(Long userId) {
		return repo.findByUser_UserId(userId);
	}

	public Token revoke(Long id) {
		Token t = findById(id);
		t.setStatus(TokenStatus.REVOKED);
		return repo.save(t);
	}


	public Token createAndSave(String email, String role, String jwtValue, String endpoint, TokenType tokenType) {
		// Look up the user by email
		User user = userRepo.findByEmail(email).orElse(null);

		Token token = new Token();
		token.setUser(user);
		token.setTokenType(tokenType);
		token.setScope(role);
		token.setTokenValue(jwtValue);
		token.setEndpoint(endpoint);
		token.setIssuedAt(LocalDateTime.now());
		token.setExpiresAt(LocalDateTime.now().plusSeconds(expirationMs / 1000));
		token.setStatus(TokenStatus.ACTIVE);

		return repo.save(token);
	}
}

