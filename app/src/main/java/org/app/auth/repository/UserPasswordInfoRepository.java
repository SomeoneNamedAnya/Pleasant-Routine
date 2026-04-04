package org.app.auth.repository;

import org.app.auth.domain.UserPasswordInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserPasswordInfoRepository extends JpaRepository<UserPasswordInfo, UUID> {
    Optional<UserPasswordInfo> findByEmail(String email);
}