package com.david.backendspringbootbots.services;

import com.david.backendspringbootbots.entities.User;
import com.david.backendspringbootbots.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String username,String email, String rawPassword) {
        List<String> errors = new ArrayList<>();
        if (userRepository.findByUsername(username).isPresent()) {
            errors.add("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            errors.add("Email already exists");
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException(String.join("; ", errors));
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
