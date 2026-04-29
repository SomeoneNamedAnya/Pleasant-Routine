package org.app.dormitory.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.app.dormitory.repository.StudentLivingRepository;
import org.app.note.RoomNote;
import org.app.note.repository.RoomNoteRepository;
import org.app.room.domain.RoomInfo;
import org.app.room.repository.RoomRepository;
import org.app.sharing.dto.PagedNews;
import org.app.sharing.dto.RoomSearchResult;
import org.app.sharing.dto.UserSearchResult;
import org.app.user.domain.User;
import org.app.user.repository.UserUserInfoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscoveryService {

    private final UserUserInfoRepository userRepo;
    private final RoomRepository roomRepo;
    private final RoomNoteRepository newsRepo;
    private final StudentLivingRepository livingRepo;

    private Long getDormitoryId(User user) {
        return livingRepo.findByUserIdAndEndedAtIsNull(user.getId())
                .map(sl -> sl.getDormitory().getId())
                .orElseThrow(() -> new RuntimeException(
                        "User is not assigned to any dormitory"));
    }

    public Page<UserSearchResult> searchPeople(
            User user, Long searchId, String searchName,
            int page, int size
    ) {
        Long dormId = getDormitoryId(user);
        Pageable pg = PageRequest.of(page, size);

        if (searchId != null) {
            return userRepo.searchById(dormId, searchId, pg)
                    .map(this::toUserResult);
        }
        return userRepo.searchByName(dormId, searchName, pg)
                .map(this::toUserResult);
    }

    private UserSearchResult toUserResult(User u) {
        return UserSearchResult.builder()
                .id(u.getId())
                .name(u.getName())
                .surname(u.getSurname())
                .lastName(u.getLastName())
                .photoLink(u.getPhotoLink())
                .roomId(u.getRoom() != null ? u.getRoom().getId() : null)
                .roomNumber(u.getRoom() != null ? u.getRoom().getNumber() : null)
                .build();
    }

    public Page<RoomSearchResult> searchRooms(
            User user, Long searchId, String searchNumber,
            int page, int size
    ) {
        Long dormId = getDormitoryId(user);
        Pageable pg = PageRequest.of(page, size);

        if (searchId != null) {
            return roomRepo.searchById(dormId, searchId, pg)
                    .map(this::toRoomResult);
        }
        return roomRepo.searchByNumber(dormId, searchNumber, pg)
                .map(this::toRoomResult);
    }

    private RoomSearchResult toRoomResult(RoomInfo r) {
        return RoomSearchResult.builder()
                .id(r.getId())
                .number(r.getNumber())
                .dormitoryId(r.getDormitory() != null ? r.getDormitory().getId() : null)
                .dormitoryName(r.getDormitory() != null ? r.getDormitory().getName() : null)
                .publicPhotoLink(r.getPublicPhotoLink())
                .build();
    }

    public PagedNews getNews(User user, int page, int size) {
        Long dormId = getDormitoryId(user);
        Pageable pg = PageRequest.of(page, size);

        Page<RoomNote> notePage = newsRepo.findPublicByDormitory(dormId, pg);

        return PagedNews.builder()
                .content(notePage.getContent().stream()
                        .map(RoomNote::toDto)
                        .toList())
                .page(notePage.getNumber())
                .size(notePage.getSize())
                .totalElements(notePage.getTotalElements())
                .totalPages(notePage.getTotalPages())
                .last(notePage.isLast())
                .build();
    }
}