package com.david.backendspringbootbots.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "bots_users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "bot_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Bot bot;
}