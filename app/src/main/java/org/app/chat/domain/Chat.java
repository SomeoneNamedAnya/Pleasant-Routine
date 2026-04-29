package org.app.chat.domain;


import jakarta.persistence.*;
import lombok.*;
import org.app.room.domain.RoomInfo;
import org.app.user.domain.User;

import java.time.Instant;


@Entity
@Table(name = "chat")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_at")
    private Instant startAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator")
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private RoomInfo room;
}