package org.app.user.repository;

import org.app.user.domain.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserUserInfoRepository extends JpaRepository<User, Long> {
    @NotNull Optional<User> findById(@NotNull Long id);
    @Query("""
        SELECT u FROM User u
        JOIN StudentLiving sl ON sl.user.id = u.id
        WHERE sl.dormitory.id = :dormId
          AND sl.endedAt IS NULL
          AND (:query IS NULL
               OR LOWER(CONCAT(u.surname,' ',u.name,' ',COALESCE(u.lastName,'')))
                  LIKE LOWER(CONCAT('%',:query,'%')))
    """)
    Page<User> searchByName(
            @Param("dormId") Long dormitoryId,
            @Param("query")  String query,
            Pageable pageable);

    @Query("""
        SELECT u FROM User u
        JOIN StudentLiving sl ON sl.user.id = u.id
        WHERE sl.dormitory.id = :dormId
          AND sl.endedAt IS NULL
          AND u.id = :userId
    """)
    Page<User> searchById(
            @Param("dormId") Long dormitoryId,
            @Param("userId") Long userId,
            Pageable pageable);
}
