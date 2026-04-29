package org.app.task.entity;

import jakarta.persistence.*;
import lombok.*;
import org.app.room.domain.RoomInfo;
import org.app.task.enums.TaskStatus;
import org.app.task.enums.TaskType;

import java.time.Instant;

@Entity
@Table(name = "task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "create_at")
    private Instant createAt;

    private Instant deadline;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TaskType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private RoomInfo room;
}