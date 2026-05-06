package com.david.backendspringbootbots.repositories;

import com.david.backendspringbootbots.entities.Bot;
import com.david.backendspringbootbots.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BotRepository extends JpaRepository<Bot, Long> {
    List<Bot> findByStateTrue();
    List<Bot> findByUser(User user);
}