package com.david.backendspringbootbots.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Deque<Long>> requests = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (!path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        String clientIp = resolveClientIp(request);
        int maxRequests;
        Duration window;

        if (path.equals("/api/users/login")) {
            maxRequests = 10;
            window = Duration.ofMinutes(15);
        } else if (path.equals("/api/users/register")) {
            maxRequests = 5;
            window = Duration.ofHours(1);
        } else {
            maxRequests = 120;
            window = Duration.ofMinutes(1);
        }

        String key = clientIp + ":" + path;
        if (!allow(key, maxRequests, window)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setHeader("Retry-After", String.valueOf(window.toSeconds()));
            response.getWriter().write("{\"message\":\"Too many requests. Please try again later.\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean allow(String key, int maxRequests, Duration window) {
        long now = System.currentTimeMillis();
        Deque<Long> times = requests.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());
        synchronized (times) {
            while (!times.isEmpty() && now - times.peekFirst() > window.toMillis()) {
                times.pollFirst();
            }
            if (times.size() >= maxRequests) return false;
            times.addLast(now);
            return true;
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
