package org.app.auth.sevices;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.app.auth.domain.RefreshToken;
import org.app.auth.domain.UserPasswordInfo;
import org.app.auth.repository.RefreshTokenRepository;
import org.app.properties.JwtProperties;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository repository;

    @Transactional
    public RefreshToken createRefreshToken(UserPasswordInfo user) {
        repository.deleteByUserPasswordInfo(user);

        RefreshToken rt = new RefreshToken();
        rt.setUserPasswordInfo(user);
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiryDate(Instant.now().plusMillis(
                jwtProperties.getRefresh().getExpiration()
        ));
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

    @Transactional
    public void deleteByToken(String token) {
        repository.deleteByToken(token);
    }
}