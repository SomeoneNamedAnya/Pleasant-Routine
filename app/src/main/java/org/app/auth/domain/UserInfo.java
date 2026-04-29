package org.app.auth.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;


@Entity
@Table(name = "User_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    @Column(unique = true)
    private String email;
    @Column(name = "education_id")
    private Long educationId;
    @Column(name = "room_id")
    private Long roomId;
    @Column(name = "role")
    private String role;
    @Column(name = "deleted_at")
    private Instant deletedAt;

    public UserInfo(String name, String surname, String lastName,
                    Date dateOfBirth, String email, Long educationId,
                    Long roomId, String role) {
        this.name = name;
        this.surname = surname;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.educationId = educationId;
        this.roomId = roomId;
        this.role = role;
    }
}