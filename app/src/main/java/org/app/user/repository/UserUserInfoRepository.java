package org.app.user.repository;

import org.app.user.domain.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserUserInfoRepository extends JpaRepository<User, Long> {
    @NotNull Optional<User> findById(@NotNull Long id);

}
