package org.app.task.entity;

import jakarta.persistence.*;
import lombok.*;
import org.app.user.domain.User;

import java.time.Instant;

@Entity
@Table(name = "task_approval")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskApprovalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watcher_id")
    private User watcher;

    private Boolean approved;

    @Column(name = "created_at")
    private Instant createdAt;
}
