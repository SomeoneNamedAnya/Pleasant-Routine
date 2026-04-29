package org.app.sharing.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.app.dormitory.domain.StudentLiving;
import org.app.dormitory.repository.StudentLivingRepository;
import org.app.ref_info.domain.Dormitory;
import org.app.sharing.domain.SharingCard;
import org.app.sharing.dto.CreateSharingCardRequest;
import org.app.sharing.dto.SharingCardDto;
import org.app.sharing.repository.SharingCardRepository;
import org.app.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class SharingService {

    private final SharingCardRepository sharingRepo;
    private final StudentLivingRepository livingRepo;

    private Long getDormitoryId(User user) {
        return livingRepo.findByUserIdAndEndedAtIsNull(user.getId())
                .map(sl -> sl.getDormitory().getId())
                .orElseThrow(() -> new RuntimeException(
                        "User is not assigned to any dormitory"));
    }

    private Dormitory getDormitory(User user) {
        return livingRepo.findByUserIdAndEndedAtIsNull(user.getId())
                .map(StudentLiving::getDormitory)
                .orElseThrow(() -> new RuntimeException(
                        "User is not assigned to any dormitory"));
    }

    public SharingCardDto create(User user, CreateSharingCardRequest req) {
        SharingCard card = SharingCard.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .photoLink(req.getPhotoLink())
                .creator(user)
                .room(user.getRoom())
                .dormitory(getDormitory(user))
                .isActive(true)
                .createdAt(Instant.now())
                .build();
        return sharingRepo.save(card).toDto();
    }

    public SharingCardDto claim(User user, Long cardId) {
        SharingCard card = sharingRepo.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getIsActive()) {
            throw new RuntimeException("Card is already claimed");
        }

        card.setClaimedBy(user);
        card.setIsActive(false);
        return sharingRepo.save(card).toDto();
    }

    public Page<SharingCardDto> myCreated(User user, int page, int size) {
        Pageable pg = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Long dormId = getDormitoryId(user);
        return sharingRepo
                .findByDormitoryIdAndCreatorId(dormId, user.getId(), pg)
                .map(SharingCard::toDto);
    }

    public Page<SharingCardDto> myClaimed(User user, int page, int size) {
        Pageable pg = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Long dormId = getDormitoryId(user);
        return sharingRepo
                .findByDormitoryIdAndClaimedById(dormId, user.getId(), pg)
                .map(SharingCard::toDto);
    }

    public Page<SharingCardDto> allActive(User user, int page, int size) {
        Pageable pg = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Long dormId = getDormitoryId(user);
        return sharingRepo
                .findByDormitoryIdAndIsActiveTrue(dormId, pg)
                .map(SharingCard::toDto);
    }

    public void delete(User user, Long cardId) {
        SharingCard card = sharingRepo.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        if (!card.getCreator().getId().equals(user.getId())) {
            throw new RuntimeException("Only creator can delete");
        }
        sharingRepo.delete(card);
    }
}