package org.app.task.entity;

import jakarta.persistence.*;
import lombok.*;
import org.app.user.domain.User;

import java.time.Instant;

@Entity
@Table(name = "task_comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String comment;

    @Column(name = "created_at")
    private Instant createdAt;
}
