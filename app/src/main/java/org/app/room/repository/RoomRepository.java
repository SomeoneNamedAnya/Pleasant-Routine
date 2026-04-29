package org.app.room.repository;

import org.app.room.domain.RoomInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomInfo, Long> {
    @NotNull Optional<RoomInfo> findById(@NotNull Long id);
    @NotNull Optional<RoomInfo> findByResidents_Id(@NotNull Long id);
    @Query("""
        SELECT r FROM RoomInfo r
        WHERE r.dormitory.id = :dormId
          AND (:number IS NULL
               OR LOWER(r.number) LIKE LOWER(CONCAT('%',:number,'%')))
    """)
    Page<RoomInfo> searchByNumber(
            @Param("dormId")  Long dormitoryId,
            @Param("number")  String number,
            Pageable pageable);

    @Query("""
        SELECT r FROM RoomInfo r
        WHERE r.dormitory.id = :dormId
          AND r.id = :roomId
    """)
    Page<RoomInfo> searchById(
            @Param("dormId") Long dormitoryId,
            @Param("roomId") Long roomId,
            Pageable pageable);

}
