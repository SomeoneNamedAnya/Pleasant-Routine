package org.app.auth.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "User_password_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(name = "hash_password", nullable = false)
    private String hashPassword;
    @Column(name = "has_to_change", nullable = false)
    private Boolean hasToChangePassword;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public UserPasswordInfo(String email, String hashPassword, Boolean hasToChangePassword,
                            UserInfo user, LocalDateTime createdAt,
                            LocalDateTime updatedAt) {
        this.email = email;
        this.hashPassword = hashPassword;
        this.hasToChangePassword = hasToChangePassword;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
