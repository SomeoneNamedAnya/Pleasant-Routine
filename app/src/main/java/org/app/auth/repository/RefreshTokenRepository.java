package org.app.auth.repository;

import org.app.auth.domain.RefreshToken;
import org.app.auth.domain.UserPasswordInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserPasswordInfo(UserPasswordInfo user);
    void deleteByToken(String token);
}
