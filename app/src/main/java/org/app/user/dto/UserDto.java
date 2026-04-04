package org.app.user.dto;

import jakarta.persistence.*;
import lombok.*;
import org.app.user.domain.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
    private Long educationId;
    private Long roomId;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private String photoLink;
    private String about;

}
