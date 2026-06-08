package com.david.backendspringbootbots.services;

import com.david.backendspringbootbots.entities.Bot;
import com.david.backendspringbootbots.repositories.BotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotExecutorService {

    private final BotRepository botRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient = RestClient.create();
    private final SseService sseService;

    @Scheduled(fixedDelay = 10000)
    public void runBots() {
        List<Bot> bots = botRepository.findByStateTrue();
        for (Bot bot : bots) {
            try {
                switch (bot.getPlatform()) {
                    case DISCORD  -> runDiscord(bot);
                    case TELEGRAM -> runTelegram(bot);
                    case REDDIT   -> runReddit(bot);
                    case YOUTUBE  -> runYouTube(bot);
                    case GITHUB   -> runGithub(bot);
                }
            } catch (Exception e) {
                log.error("Error executing bot id={} platform={}: {}", bot.getId(), bot.getPlatform(), e.getMessage());
            }
        }
    }

    private void runDiscord(Bot bot) {
        String base = "https://discord.com/api/v10";
        String auth = "Bot " + bot.getOauthToken();

        switch (bot.getType()) {
            case LISTENER, BOTH -> {
                ObjectNode cfg = parseConfig(bot);
                String lastId = cfg.has("lastMessageId") ? cfg.get("lastMessageId").asText("0") : "0";
                String response = restClient.get()
                        .uri(base + "/channels/" + bot.getTarget() + "/messages?limit=10&after=" + lastId)
                        .header("Authorization", auth)
                        .retrieve()
                        .body(String.class);
                JsonNode messages = parseJson(response);
                if (messages.isArray() && !messages.isEmpty()) {
                    String newestId = messages.get(0).get("id").asText();
                    for (JsonNode msg : messages) {
                        sseService.sendEvent("[DISCORD][" + bot.getName() + "] " + msg.get("content").asText(""));
                    }
                    cfg.put("lastMessageId", newestId);
                    saveConfig(bot, cfg);
                }
            }
        }

        switch (bot.getType()) {
            case SENDER, BOTH -> {
                ObjectNode cfg = parseConfig(bot);
                String message = cfg.has("message") ? cfg.get("message").asText("") : "";
                if (message.isBlank()) return;
                String body = objectMapper.createObjectNode().put("content", message).toString();
                restClient.post()
                        .uri(base + "/channels/" + bot.getTarget() + "/messages")
                        .header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .toBodilessEntity();
                sseService.sendEvent("[DISCORD][" + bot.getName() + "] Message sent");
                cfg.put("message", "");
                saveConfig(bot, cfg);
            }
        }
    }

    private void runTelegram(Bot bot) {
        String base = "https://api.telegram.org/bot" + bot.getOauthToken();

        switch (bot.getType()) {
            case LISTENER, BOTH -> {
                ObjectNode cfg = parseConfig(bot);
                long lastUpdateId = cfg.has("lastUpdateId") ? cfg.get("lastUpdateId").asLong(0) : 0;
                String response = restClient.get()
                        .uri(base + "/getUpdates?offset=" + (lastUpdateId + 1) + "&limit=10&timeout=0")
                        .retrieve()
                        .body(String.class);
                JsonNode root = parseJson(response);
                JsonNode updates = root.get("result");
                if (updates != null && updates.isArray() && !updates.isEmpty()) {
                    for (JsonNode update : updates) {
                        long updateId = update.get("update_id").asLong();
                        if (updateId > lastUpdateId) lastUpdateId = updateId;
                        JsonNode msgNode = update.get("message");
                        if (msgNode != null) {
                            String chatId = msgNode.get("chat").get("id").asText();
                            String text = msgNode.has("text") ? msgNode.get("text").asText("") : "";
                            if (bot.getTarget().isBlank() || bot.getTarget().equals(chatId)) {
                                sseService.sendEvent("[TELEGRAM][" + bot.getName() + "] " + text);
                            }
                        }
                    }
                    cfg.put("lastUpdateId", lastUpdateId);
                    saveConfig(bot, cfg);
                }
            }
        }

        switch (bot.getType()) {
            case SENDER, BOTH -> {
                ObjectNode cfg = parseConfig(bot);
                String message = cfg.has("message") ? cfg.get("message").asText("") : "";
                if (message.isBlank() || bot.getTarget().isBlank()) return;
                ObjectNode body = objectMapper.createObjectNode();
                body.put("chat_id", bot.getTarget());
                body.put("text", message);
                restClient.post()
                        .uri(base + "/sendMessage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body.toString())
                        .retrieve()
                        .toBodilessEntity();
                sseService.sendEvent("[TELEGRAM][" + bot.getName() + "] Message sent");
                cfg.put("message", "");
                saveConfig(bot, cfg);
            }
        }
    }

    private void runReddit(Bot bot) {
        ObjectNode cfg = parseConfig(bot);
        String accessToken = bot.getOauthToken();
        if (bot.getRefreshToken() != null && !bot.getRefreshToken().isBlank()) {
            accessToken = refreshRedditToken(bot, cfg);
        }
        final String token = accessToken;

        switch (bot.getType()) {
            case LISTENER, BOTH -> {
                String lastPostId = cfg.has("lastPostId") ? cfg.get("lastPostId").asText("") : "";
                String url = "https://oauth.reddit.com/r/" + bot.getTarget() + "/new.json?limit=10";
                if (!lastPostId.isBlank()) url += "&before=" + lastPostId;
                String response = restClient.get()
                        .uri(url)
                        .header("Authorization", "Bearer " + token)
                        .header("User-Agent", "SpringBot/1.0")
                        .retrieve()
                        .body(String.class);
                JsonNode posts = parseJson(response).path("data").path("children");
                if (posts.isArray() && !posts.isEmpty()) {
                    String newestId = posts.get(0).path("data").get("name").asText();
                    for (JsonNode post : posts) {
                        sseService.sendEvent("[REDDIT][" + bot.getName() + "] " + post.get("data").get("title").asText(""));
                    }
                    cfg.put("lastPostId", newestId);
                    saveConfig(bot, cfg);
                }
            }
        }

        switch (bot.getType()) {
            case SENDER, BOTH -> {
                String title = cfg.has("title") ? cfg.get("title").asText("") : "";
                String text  = cfg.has("text")  ? cfg.get("text").asText("") : "";
                if (title.isBlank()) return;
                restClient.post()
                        .uri("https://oauth.reddit.com/api/submit")
                        .header("Authorization", "Bearer " + token)
                        .header("User-Agent", "SpringBot/1.0")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body("sr=" + bot.getTarget() + "&kind=self&title=" + title + "&text=" + text + "&api_type=json")
                        .retrieve()
                        .toBodilessEntity();
                sseService.sendEvent("[REDDIT][" + bot.getName() + "] Post sent");
                cfg.put("title", "");
                cfg.put("text", "");
                saveConfig(bot, cfg);
            }
        }
    }

    private String refreshRedditToken(Bot bot, ObjectNode cfg) {
        String clientId = cfg.has("clientId") ? cfg.get("clientId").asText("") : "";
        String clientSecret = cfg.has("clientSecret") ? cfg.get("clientSecret").asText("") : "";
        if (clientId.isBlank() || clientSecret.isBlank()) return bot.getOauthToken();
        String credentials = java.util.Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        String response = restClient.post()
                .uri("https://www.reddit.com/api/v1/access_token")
                .header("Authorization", "Basic " + credentials)
                .header("User-Agent", "SpringBot/1.0")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=refresh_token&refresh_token=" + bot.getRefreshToken())
                .retrieve()
                .body(String.class);
        JsonNode node = parseJson(response);
        String newToken = node.has("access_token") ? node.get("access_token").asText() : bot.getOauthToken();
        bot.setOauthToken(newToken);
        botRepository.save(bot);
        return newToken;
    }

    private void runYouTube(Bot bot) {
        ObjectNode cfg = parseConfig(bot);
        String apiKey = bot.getOauthToken();
        String lastVideoId = cfg.has("lastVideoId") ? cfg.get("lastVideoId").asText("") : "";
        if (apiKey.isBlank()) return;
        String response = restClient.get()
                .uri("https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + bot.getTarget() + "&order=date&maxResults=5&type=video&key=" + apiKey)
                .retrieve()
                .body(String.class);
        JsonNode items = parseJson(response).path("items");
        if (items.isArray() && !items.isEmpty()) {
            String newestId = items.get(0).path("id").path("videoId").asText();
            if (!newestId.equals(lastVideoId)) {
                for (JsonNode item : items) {
                    String videoId = item.path("id").path("videoId").asText();
                    if (videoId.equals(lastVideoId)) break;
                    sseService.sendEvent("[YOUTUBE][" + bot.getName() + "] New video: " + item.path("snippet").path("title").asText(""));
                }
                cfg.put("lastVideoId", newestId);
                saveConfig(bot, cfg);
            }
        }
    }

    private void runGithub(Bot bot) {
        ObjectNode cfg = parseConfig(bot);
        String lastEventId = cfg.has("lastEventId") ? cfg.get("lastEventId").asText("") : "";
        String response = restClient.get()
                .uri("https://api.github.com/repos/" + bot.getTarget() + "/events?per_page=10")
                .header("Authorization", "Bearer " + bot.getOauthToken())
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .body(String.class);
        JsonNode events = parseJson(response);
        if (events.isArray() && !events.isEmpty()) {
            String newestId = events.get(0).get("id").asText();
            if (newestId.equals(lastEventId)) return;
            for (JsonNode event : events) {
                if (event.get("id").asText().equals(lastEventId)) break;
                handleGithubEvent(event, bot);
            }
            cfg.put("lastEventId", newestId);
            saveConfig(bot, cfg);
        }
    }

    private void handleGithubEvent(JsonNode event, Bot bot) {
        String type  = event.get("type").asText("");
        String actor = event.path("actor").path("login").asText("");
        switch (type) {
            case "WatchEvent"        -> sseService.sendEvent("[GITHUB][" + bot.getName() + "] Star by " + actor);
            case "ForkEvent"         -> sseService.sendEvent("[GITHUB][" + bot.getName() + "] Fork by " + actor);
            case "PushEvent"         -> sseService.sendEvent("[GITHUB][" + bot.getName() + "] " + event.path("payload").path("commits").size() + " commits to " + event.path("payload").path("ref").asText("") + " by " + actor);
            case "IssuesEvent"       -> sseService.sendEvent("[GITHUB][" + bot.getName() + "] Issue " + event.path("payload").path("action").asText("") + ": " + event.path("payload").path("issue").path("title").asText("") + " by " + actor);
            case "IssueCommentEvent" -> sseService.sendEvent("[GITHUB][" + bot.getName() + "] Comment by " + actor + ": " + event.path("payload").path("comment").path("body").asText(""));
            case "PullRequestEvent"  -> sseService.sendEvent("[GITHUB][" + bot.getName() + "] PR " + event.path("payload").path("number").asInt() + " " + event.path("payload").path("action").asText("") + " by " + actor);
            case "ReleaseEvent"      -> sseService.sendEvent("[GITHUB][" + bot.getName() + "] Release " + event.path("payload").path("release").path("tag_name").asText("") + " by " + actor);
            default                  -> sseService.sendEvent("[GITHUB][" + bot.getName() + "] " + type + " by " + actor);
        }
    }

    private ObjectNode parseConfig(Bot bot) {
        String json = bot.getExtraConfigJson();
        if (json == null || json.isBlank()) return objectMapper.createObjectNode();
        try {
            return (ObjectNode) objectMapper.readTree(json);
        } catch (Exception e) {
            log.warn("Invalid config JSON for bot {}, using empty config", bot.getId());
            return objectMapper.createObjectNode();
        }
    }

    private void saveConfig(Bot bot, ObjectNode config) {
        try {
            bot.setExtraConfigJson(objectMapper.writeValueAsString(config));
            botRepository.save(bot);
        } catch (Exception e) {
            log.error("Failed to save config for bot {}", bot.getId());
        }
    }

    private JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            log.error("Error parsing JSON: {}", e.getMessage());
            return objectMapper.createObjectNode();
        }
    }
}