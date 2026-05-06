package com.david.backendspringbootbots.controllers;

import com.david.backendspringbootbots.entities.User;
import com.david.backendspringbootbots.services.JwtService;
import com.david.backendspringbootbots.services.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    public record UserResponse(Long id, String username, String email) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request.username(), request.email(), request.password());
            return ResponseEntity.ok(new UserResponse(user.getId(), user.getUsername(), user.getEmail()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }


    public record RegisterRequest(String username, String email, String password) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Optional<User> userOpt = userService.login(request.username(), request.password());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        User user = userOpt.get();
        String token = jwtService.generateToken(user);

        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(604800);
        boolean isProd = "prod".equals(System.getenv("SPRING_PROFILES_ACTIVE"));
        if (isProd) {
            cookie.setSecure(true);
            cookie.setAttribute("SameSite", "None");
        } else {
            cookie.setSecure(false);
            cookie.setAttribute("SameSite", "Lax");
        }
        response.addCookie(cookie);

        return ResponseEntity.ok(new UserResponse(user.getId(), user.getUsername(), user.getEmail()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        boolean isProd = "prod".equals(System.getenv("SPRING_PROFILES_ACTIVE"));
        if (isProd) cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                try {
                    Claims claims = jwtService.validateToken(cookie.getValue());
                    return ResponseEntity.ok(new UserResponse(
                            claims.get("id", Long.class),
                            claims.getSubject(),
                            claims.get("email", String.class)
                    ));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

        public record LoginRequest(String username, String password) {}

}
