package org.app.user.service;

import org.apache.coyote.BadRequestException;
import org.app.user.client.ClientStorage;
import org.app.user.domain.User;
import org.app.user.repository.UserUserInfoRepository;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Component
public class UserService {
    private final UserUserInfoRepository userInfoRepository;
    private final ClientStorage clientStorage;

    public UserService(UserUserInfoRepository userInfoRepository, ClientStorage clientStorage) {
        this.userInfoRepository = userInfoRepository;
        this.clientStorage = clientStorage;
    }

    public User getUserInfo(Long id) {

        return userInfoRepository.findById(id).orElse(null);
    }

    public void setAbout(Long id, String about) {
        User user = userInfoRepository.findById(id).orElse(null);
        System.out.println(user);
        if (user != null) {
            user.setAbout(about);
            userInfoRepository.save(user);
        }
    }

    public String setPhoto(Long id, MultipartFile file) throws IOException {

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

        String fileUrl = key;

        User user = userInfoRepository.findById(id).orElse(null);
        if (user == null) {
            throw new BadRequestException("User не найден");
        }
        user.setPhotoLink(fileUrl);
        userInfoRepository.save(user);

        return fileUrl;

    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
