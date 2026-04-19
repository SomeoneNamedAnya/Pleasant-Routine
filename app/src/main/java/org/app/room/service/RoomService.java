package org.app.room.service;

import org.app.room.domain.RoomInfo;
import org.app.room.repository.RoomRepository;
import org.springframework.stereotype.Component;

@Component
public class RoomService {
    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public RoomInfo getSelfRoomInfo(Long id) {
        return repository.findByResidents_Id(id).orElse(null);
    }


}
