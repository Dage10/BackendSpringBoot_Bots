package com.david.backendspringbootbots.entities;

import com.david.backendspringbootbots.domain.TypeBot;
import com.david.backendspringbootbots.domain.Platform;
import com.david.backendspringbootbots.security.BotEncryptionConverter;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false,length = 80)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeBot type;

    @Column(nullable = false)
    private String target;

    @Column(nullable = false)
    private Boolean state;

    @Convert(converter = BotEncryptionConverter.class)
    private String oauthToken;

    @Convert(converter = BotEncryptionConverter.class)
    private String refreshToken;

    @Convert(converter = BotEncryptionConverter.class)
    private String extraConfigJson;
}
