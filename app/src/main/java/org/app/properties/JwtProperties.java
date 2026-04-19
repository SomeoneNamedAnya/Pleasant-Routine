package org.app.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jwt")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Component
public class JwtProperties {
    @NotEmpty
    private String secret;

    private Expiration access = new Expiration();
    private Expiration refresh = new Expiration();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Expiration {
        private long expiration;
    }
}
