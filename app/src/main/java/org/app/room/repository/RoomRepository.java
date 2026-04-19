package org.app.room.repository;

import org.app.room.domain.RoomInfo;
import org.app.user.domain.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomInfo, Long> {
    @NotNull Optional<RoomInfo> findById(@NotNull Long id);
    @NotNull Optional<RoomInfo> findByResidents_Id(@NotNull Long id);


}
