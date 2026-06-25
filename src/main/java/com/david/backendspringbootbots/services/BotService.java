package com.david.backendspringbootbots.services;

import com.david.backendspringbootbots.controllers.BotController;
import com.david.backendspringbootbots.entities.Bot;
import com.david.backendspringbootbots.entities.User;
import com.david.backendspringbootbots.repositories.BotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BotService {

    private final BotRepository botRepository;

    public Bot create(User user, Bot bot) {
        bot.setUser(user);
        return botRepository.save(bot);
    }

    public List<Bot> getBotsByUser(User user) {
        return botRepository.findByUser(user);
    }

    public void toggleState(User user, Long id) {
        Bot bot = getByIdForUser(user, id);
        bot.setState(!bot.getState());
        botRepository.save(bot);
    }

    public Bot update(User user, Long id, BotController.BotRequest req) {
        Bot bot = getByIdForUser(user, id);

        bot.setName(req.name());
        bot.setType(req.type());
        bot.setTarget(req.target());
        bot.setPlatform(req.platform());

        if (req.oauthToken() != null && !req.oauthToken().isBlank()) {
            bot.setOauthToken(req.oauthToken());
        }
        if (req.refreshToken() != null && !req.refreshToken().isBlank()) {
            bot.setRefreshToken(req.refreshToken());
        }
        if (req.extraConfigJson() != null) {
            bot.setExtraConfigJson(req.extraConfigJson());
        }

        return botRepository.save(bot);
    }

    @Transactional
    public void delete(User user, Long id) {
        botRepository.delete(getByIdForUser(user, id));
    }

    public Bot getByIdForUser(User user, Long id) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bot not found"));
        if (!bot.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        return bot;
    }

    public void updateMessage(Bot bot, String message) {
        try {
            ObjectNode cfg = (ObjectNode) new ObjectMapper()
                    .readTree(bot.getExtraConfigJson() == null ? "{}" : bot.getExtraConfigJson());
            cfg.put("message", message);
            bot.setExtraConfigJson(cfg.toString());
            botRepository.save(bot);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid config JSON");
        }
    }
}
