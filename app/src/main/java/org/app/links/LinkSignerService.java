package org.app.links;

import org.app.user.client.ClientStorage;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Component
public class LinkSignerService {
    private final ClientStorage clientStorage;

    public LinkSignerService(ClientStorage clientStorage) {
        this.clientStorage = clientStorage;
    }

    public String sign(String link) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(clientStorage.getBacketName())
                .key(link)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(60))
                        .getObjectRequest(getObjectRequest)
                        .build();

        PresignedGetObjectRequest presignedRequest =
                clientStorage.getPresigner().presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }
}
