package com.david.backendspringbootbots.services;

import com.david.backendspringbootbots.entities.User;
import com.david.backendspringbootbots.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String username, String email, String rawPassword) {
        List<String> errors = new ArrayList<>();

        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            errors.add("Username must be 3-50 alphanumeric characters or underscores");
        }
        if (rawPassword == null || rawPassword.length() < 8) {
            errors.add("Password must be at least 8 characters long");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            errors.add("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            errors.add("Email already exists");
        }

        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join("; ", errors));
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .build();
        return userRepository.save(user);
    }

    public Optional<User> login(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
