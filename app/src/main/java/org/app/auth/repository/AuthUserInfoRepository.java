package org.app.auth.repository;

import org.app.auth.domain.UserInfo;
import org.app.auth.domain.UserPasswordInfo;
import org.app.user.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByEmail(String email);
}