package org.app.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.app.user.domain.User;

import java.time.Instant;

@Entity
@Table(name = "message_in_chat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageInChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "start_dt")
    private Instant startDt;

    private String message;
}
