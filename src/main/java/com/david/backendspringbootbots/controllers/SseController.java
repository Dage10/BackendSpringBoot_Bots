package com.david.backendspringbootbots.controllers;

import com.david.backendspringbootbots.security.AuthService;
import com.david.backendspringbootbots.services.SseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;
    private final AuthService authService;

    @GetMapping("/stream")
    public SseEmitter stream(HttpServletRequest request) {
        return sseService.subscribe(authService.requireUser(request).getId());
    }
}
