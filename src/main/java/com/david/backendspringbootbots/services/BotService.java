package com.david.backendspringbootbots.services;

import com.david.backendspringbootbots.controllers.BotController;
import com.david.backendspringbootbots.entities.Bot;
import com.david.backendspringbootbots.entities.User;
import com.david.backendspringbootbots.repositories.BotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        Bot bot = botRepository.findById(id).orElseThrow();

        if(!bot.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Not allowed");
        }

        bot.setState(!bot.getState());
        botRepository.save(bot);
    }

    public Bot update(User user, Long id, BotController.BotRequest req) {
        Bot bot = botRepository.findById(id).orElseThrow();

        if(!bot.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Not allowed");
        }

        bot.setName(req.name());
        bot.setType(req.type());
        bot.setTarget(req.target());
        bot.setOauthToken(req.oauthToken());
        bot.setExtraConfigJson(req.extraConfigJson());
        bot.setPlatform(req.platform());

        return botRepository.save(bot);
    }

    @Transactional
    public void delete(User user, Long id) {
        Bot bot = botRepository.findById(id).orElseThrow();
        if(!bot.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Not allowed");
        }
        botRepository.delete(bot);
    }

    public Bot getByIdForUser(User user, Long id) {
        Bot bot = botRepository.findById(id).orElseThrow();
        if (!bot.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
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
            throw new RuntimeException("Invalid config JSON");
        }
    }


}
