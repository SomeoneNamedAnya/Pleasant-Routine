package org.app.room.service;

import org.apache.coyote.BadRequestException;
import org.app.room.domain.RoomInfo;
import org.app.room.repository.RoomRepository;
import org.app.user.client.ClientStorage;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Component
public class RoomService {
    private final RoomRepository repository;
    private final ClientStorage clientStorage;

    public RoomService(RoomRepository repository, ClientStorage clientStorage) {
        this.repository = repository;
        this.clientStorage = clientStorage;
    }

    public RoomInfo getSelfRoomInfo(Long id) {
        return repository.findByResidents_Id(id).orElse(null);
    }

    public RoomInfo getRoomInfoById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void updatePrivateInfo(Long userId, String text) {
        RoomInfo roomInfo = repository.findByResidents_Id(userId)
                .orElseThrow(() -> new RuntimeException("Комната не найдена"));
        roomInfo.setPrivateInfo(text);
        repository.save(roomInfo);
    }

    public void updatePublicInfo(Long userId, String text) {
        RoomInfo roomInfo = repository.findByResidents_Id(userId)
                .orElseThrow(() -> new RuntimeException("Комната не найдена"));
        roomInfo.setPublicInfo(text);
        repository.save(roomInfo);
    }

    public String setPhoto(Long userId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("Файл пустой");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Не тот формат");
        }
        String extension = getExtension(file.getOriginalFilename());
        String key = "photos/" + UUID.randomUUID() + "." + extension;

        clientStorage.getClient().putObject(
                PutObjectRequest.builder()
                        .bucket(clientStorage.getBacketName())
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        RoomInfo roomInfo = repository.findByResidents_Id(userId)
                .orElseThrow(() -> new RuntimeException("Комната не найдена"));
        roomInfo.setPublicPhotoLink(key);
        repository.save(roomInfo);
        return key;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
