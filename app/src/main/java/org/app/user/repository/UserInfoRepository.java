package org.app.user.repository;

import org.app.user.domain.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<User, Long> {
    @NotNull Optional<User> findById(@NotNull Long id);

}
