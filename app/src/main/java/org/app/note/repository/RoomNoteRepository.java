package org.app.note.repository;

import org.app.auth.domain.UserInfo;
import org.app.note.RoomNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomNoteRepository extends JpaRepository<RoomNote, Long>, JpaSpecificationExecutor<RoomNote> {

    @Query("select distinct t.tag from TagRoomNote t")
    List<String> findAllTags();

    @Query("select distinct r.creator from RoomNote r")
    List<UserInfo> findAllUsers();

    @Query("""
        SELECT n FROM RoomNote n
        JOIN RoomInfo r ON r.id = n.roomId
        WHERE r.dormitory.id = :dormId
          AND n.isPublic = true
        ORDER BY n.createdAt DESC
    """)
    Page<RoomNote> findPublicByDormitory(
            @Param("dormId") Long dormitoryId,
            Pageable pageable);
}