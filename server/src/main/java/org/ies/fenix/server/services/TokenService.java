package org.ies.fenix.server.services;

import org.ies.fenix.server.models.AuthToken;
import org.ies.fenix.server.repositories.AuthTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TokenService {

    private static final long TOKEN_TTL_SECONDS = 60 * 60 * 24; // 24h

    @Autowired
    private AuthTokenRepository authTokenRepository;

    public String generateToken(Integer clientId) {
        String tokenValue = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");

        AuthToken token = AuthToken.builder()
                .token(tokenValue)
                .clientId(clientId)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(TOKEN_TTL_SECONDS))
                .build();

        authTokenRepository.save(token);
        return tokenValue;
    }

    public boolean isValid(String tokenValue) {
        return authTokenRepository.findByToken(tokenValue)
                .filter(token -> token.getRevokedAt() == null)
                .filter(token -> token.getExpiresAt().isAfter(Instant.now()))
                .isPresent();
    }

    public Integer getClientId(String tokenValue) {
        return authTokenRepository.findByToken(tokenValue)
                .filter(token -> token.getRevokedAt() == null)
                .filter(token -> token.getExpiresAt().isAfter(Instant.now()))
                .map(AuthToken::getClientId)
                .orElse(null);
    }

    public void revoke(String tokenValue) {
        authTokenRepository.findByToken(tokenValue).ifPresent(token -> {
            token.setRevokedAt(Instant.now());
            authTokenRepository.save(token);
        });
    }
}