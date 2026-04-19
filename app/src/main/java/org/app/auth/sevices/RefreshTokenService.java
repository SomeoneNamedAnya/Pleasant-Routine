package org.app.auth.sevices;

import org.app.auth.domain.RefreshToken;
import org.app.auth.domain.UserPasswordInfo;
import org.app.auth.repository.RefreshTokenRepository;
import org.app.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private JwtProperties jwtProperties;

    private final long refreshExpiration;

    private final RefreshTokenRepository repository;

    public RefreshTokenService(JwtProperties jwtProperties, RefreshTokenRepository repository) {
        this.jwtProperties = jwtProperties;
        this.repository = repository;
        this.refreshExpiration = jwtProperties.getRefresh().getExpiration();
    }

    public RefreshToken createRefreshToken(UserPasswordInfo user) {
        repository.deleteByUserPasswordInfo(user);

        RefreshToken rt = new RefreshToken();
        rt.setUserPasswordInfo(user);
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiryDate(Instant.now().plusMillis(refreshExpiration));
        return repository.save(rt);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            repository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    public void deleteByToken(String token) {
        repository.deleteByToken(token);
    }
}