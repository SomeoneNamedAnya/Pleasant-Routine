package org.app.user.client;

import lombok.Getter;
import org.app.properties.S3Properties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Component
public class ClientStorage {
    private final S3Properties s3Properties;
    @Getter
    private final String backetName = "pleasantroutinestorage";

    private final S3Client s3;
    @Getter
    private final S3Presigner presigner;

    public ClientStorage(S3Properties s3Properties) {
        this.s3Properties = s3Properties;
        this.s3 = S3Client.builder()
                .endpointOverride(java.net.URI.create("https://storage.yandexcloud.net"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        this.s3Properties.getAccess(),
                                        this.s3Properties.getSecret())
                        )
                )
                .region(Region.US_EAST_1)
                .build();
        this.presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                this.s3Properties.getAccess(),
                                this.s3Properties.getSecret())
                        )
                )
                .endpointOverride(URI.create("https://storage.yandexcloud.net"))
                .build();
    }

    public S3Client getClient() {
        return s3;
    }

}
