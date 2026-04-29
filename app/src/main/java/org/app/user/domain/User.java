package org.app.user.domain;


import jakarta.persistence.*;
import lombok.*;
import org.app.auth.domain.UserInfo;
import org.app.ref_info.domain.EducationProgram;
import org.app.room.domain.RoomInfo;
import org.app.user.dto.UserDto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Entity
@Table(name = "user_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private Instant dateOfBirth;

    @Column(unique = true)
    private String email;


    @ManyToOne
    @JoinColumn(name = "education_id")
    private EducationProgram education;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomInfo room;

    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "photo_link")
    private String photoLink;

    private String about;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public UserDto toDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDate = dateOfBirth != null
                ? dateOfBirth
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(formatter)
                : null;

        return new UserDto(
                id,
                name,
                surname,
                lastName,
                formattedDate,
                email,
                room != null ? room.getId() : null,
                room != null ? room.getNumber() : null,

                education != null ? education.getDepartment() : null,
                education != null ? education.getName() : null,
                education != null ? education.getLevel() : null,

                about,
                photoLink,
                UserRole.USER
        );
    }

    public UserInfo toUserInfo() {
        return new UserInfo(
                name,
                surname,
                lastName,
                Date.from(dateOfBirth),
                email,
                education.getId(),
                room.getId(),
                role.name()
        );
    }

}