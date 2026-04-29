package org.app.auth.repository;

import org.app.auth.domain.UserPasswordInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPasswordInfoRepository extends JpaRepository<UserPasswordInfo, Long> {
    Optional<UserPasswordInfo> findByEmail(String email);
}