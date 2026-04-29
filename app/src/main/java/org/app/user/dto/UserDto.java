package org.app.user.dto;

import jakarta.persistence.*;
import lombok.*;
import org.app.user.domain.UserRole;


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
    private String dateOfBirth;
    private String email;

    private Long roomId;
    private String roomNumber;

    private String department;

    private String educationalProgram;
    private String educationLevel;

    private String about;
    private String photoLink;
    @Enumerated(EnumType.STRING)
    private UserRole role;

}
