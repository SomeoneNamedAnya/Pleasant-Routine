package org.app.sharing.repository;

import org.app.sharing.domain.SharingCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharingCardRepository extends JpaRepository<SharingCard, Long> {

    Page<SharingCard> findByDormitoryIdAndCreatorId(
            Long dormitoryId, Long creatorId, Pageable pageable);

    Page<SharingCard> findByDormitoryIdAndClaimedById(
            Long dormitoryId, Long userId, Pageable pageable);

    Page<SharingCard> findByDormitoryIdAndIsActiveTrue(
            Long dormitoryId, Pageable pageable);
}