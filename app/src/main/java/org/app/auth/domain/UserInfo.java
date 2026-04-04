package org.app.auth.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "User_info")
@Getter
@Setter
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private UserPasswordInfo userPasswordInfo;

    private String name;
    private String surname;
    private String lastName;
    private Date dateOfBirth;
    @Column(unique = true)
    private String email;
    private Integer educationId;
    private Integer roomId;
    private String role;
    private LocalDateTime deletedAt;
}