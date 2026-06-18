package com.david.backendspringbootbots.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.david.backendspringbootbots.domain.Platform;
import com.david.backendspringbootbots.domain.TypeBot;
import com.david.backendspringbootbots.entities.Bot;
import com.david.backendspringbootbots.entities.User;
import com.david.backendspringbootbots.repositories.UserRepository;
import com.david.backendspringbootbots.services.BotService;
import com.david.backendspringbootbots.services.JwtService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/bots")
@RequiredArgsConstructor
public class BotController {
    private final BotService botService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    private User getUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("token")) {
                    Claims claims = jwtService.validateToken(c.getValue());
                    return userRepository.findById(
                            claims.get("id", Long.class)
                    ).orElseThrow();
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    public record BotRequest(String name, Platform platform, TypeBot type,
    String target, String oauthToken, String refreshToken, String extraConfigJson) {}


    @PostMapping
    public Bot create(@RequestBody BotRequest request, HttpServletRequest req) {
        User user = getUser(req);

        Bot bot = Bot.builder()
                .name(request.name())
                .platform(request.platform())
                .type(request.type())
                .target(request.target())
                .oauthToken(request.oauthToken())
                .refreshToken(request.refreshToken())
                .extraConfigJson(request.extraConfigJson())
                .state(true)
                .build();

        return botService.create(user, bot);
    }

    @PostMapping("/{id}/send")
    public void sendMessage(
            @PathVariable Long id,
            @RequestBody MessageRequest req,
            HttpServletRequest request
    ) {
        User user = getUser(request);
        Bot bot = botService.getByIdForUser(user, id);

        botService.updateMessage(bot, req.message());
    }

    public record MessageRequest(String message) {}



    @GetMapping
    public List<Bot> listBots(HttpServletRequest req) {
        return botService.getBotsByUser(getUser(req));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpServletRequest req) {
        botService.delete(getUser(req), id);
    }

    @PutMapping("/{id}")
    public Bot update(@PathVariable Long id,
                      @RequestBody BotRequest request,
                      HttpServletRequest req) {
        User user = getUser(req);
        return botService.update(user, id, request);
    }


    @PutMapping("/{id}/state")
    public void toggleState(@PathVariable Long id, HttpServletRequest req) {
        User user = getUser(req);
        botService.toggleState(user, id);
    }
}
