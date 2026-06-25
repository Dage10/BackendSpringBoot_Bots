package com.david.backendspringbootbots.security;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, Long> lockedUntil = new ConcurrentHashMap<>();

    public boolean isLocked(String username) {
        Long until = lockedUntil.get(username);
        if (until == null) return false;
        if (System.currentTimeMillis() < until) return true;
        lockedUntil.remove(username);
        attempts.remove(username);
        return false;
    }

    public void loginFailed(String username) {
        int count = attempts.merge(username, 1, Integer::sum);
        if (count >= MAX_ATTEMPTS) {
            lockedUntil.put(username, System.currentTimeMillis() + LOCK_DURATION.toMillis());
        }
    }

    public void loginSucceeded(String username) {
        attempts.remove(username);
        lockedUntil.remove(username);
    }
}
