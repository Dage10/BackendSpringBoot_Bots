package com.david.backendspringbootbots.controllers;

import com.david.backendspringbootbots.domain.Platform;
import com.david.backendspringbootbots.domain.TypeBot;
import com.david.backendspringbootbots.entities.Bot;
import com.david.backendspringbootbots.entities.User;
import com.david.backendspringbootbots.security.AuthService;
import com.david.backendspringbootbots.services.BotService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bots")
@RequiredArgsConstructor
public class BotController {

    private final BotService botService;
    private final AuthService authService;

    public record BotRequest(
            @NotBlank @Size(max = 80) String name,
            @NotNull Platform platform,
            @NotNull TypeBot type,
            @NotBlank @Size(max = 500) String target,
            @Size(max = 2000) String oauthToken,
            @Size(max = 2000) String refreshToken,
            @Size(max = 5000) String extraConfigJson
    ) {}

    public record MessageRequest(@NotBlank @Size(max = 2000) String message) {}

    public record BotResponse(Long id, String name, Platform platform, TypeBot type, String target, Boolean state) {
        static BotResponse from(Bot bot) {
            return new BotResponse(bot.getId(), bot.getName(), bot.getPlatform(), bot.getType(), bot.getTarget(), bot.getState());
        }
    }

    public record BotDetailResponse(
            Long id, String name, Platform platform, TypeBot type, String target, Boolean state,
            String oauthToken, String refreshToken, String extraConfigJson
    ) {
        static BotDetailResponse from(Bot bot) {
            return new BotDetailResponse(
                    bot.getId(), bot.getName(), bot.getPlatform(), bot.getType(), bot.getTarget(), bot.getState(),
                    bot.getOauthToken(), bot.getRefreshToken(), bot.getExtraConfigJson()
            );
        }
    }

    @PostMapping
    public BotResponse create(@Valid @RequestBody BotRequest request, HttpServletRequest req) {
        User user = authService.requireUser(req);

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

        return BotResponse.from(botService.create(user, bot));
    }

    @GetMapping
    public List<BotResponse> listBots(HttpServletRequest req) {
        return botService.getBotsByUser(authService.requireUser(req)).stream()
                .map(BotResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public BotDetailResponse getBot(@PathVariable Long id, HttpServletRequest req) {
        return BotDetailResponse.from(botService.getByIdForUser(authService.requireUser(req), id));
    }

    @PostMapping("/{id}/send")
    public void sendMessage(@PathVariable Long id, @Valid @RequestBody MessageRequest request, HttpServletRequest req) {
        User user = authService.requireUser(req);
        Bot bot = botService.getByIdForUser(user, id);
        botService.updateMessage(bot, request.message());
    }

    @PutMapping("/{id}")
    public BotResponse update(@PathVariable Long id, @Valid @RequestBody BotRequest request, HttpServletRequest req) {
        return BotResponse.from(botService.update(authService.requireUser(req), id, request));
    }

    @PutMapping("/{id}/state")
    public void toggleState(@PathVariable Long id, HttpServletRequest req) {
        botService.toggleState(authService.requireUser(req), id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpServletRequest req) {
        botService.delete(authService.requireUser(req), id);
    }
}
