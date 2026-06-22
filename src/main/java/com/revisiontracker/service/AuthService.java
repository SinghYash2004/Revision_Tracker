package com.revisiontracker.service;

import com.revisiontracker.dto.AuthRequest;
import com.revisiontracker.dto.AuthResponse;
import com.revisiontracker.model.UserAccount;
import com.revisiontracker.storage.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository users;

    public AuthService(UserRepository users) {
        this.users = users;
    }

    public AuthResponse register(AuthRequest request) {
        validate(request);
        users.findByEmailIgnoreCase(request.getEmail()).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account already exists for this email.");
        });
        UserAccount user = new UserAccount(
                UUID.randomUUID().toString(),
                blank(request.getName()) ? request.getEmail().trim() : request.getName().trim(),
                request.getEmail().trim().toLowerCase(),
                hash(request.getPassword()),
                LocalDateTime.now()
        );
        users.save(user);
        return response(user);
    }

    public AuthResponse login(AuthRequest request) {
        if (blank(request.getEmail()) || blank(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required.");
        }
        UserAccount user = users.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));
        if (!user.getPasswordHash().equals(hash(request.getPassword()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }
        return response(user);
    }

    private static AuthResponse response(UserAccount user) {
        return new AuthResponse(user.getId(), user.getName(), user.getEmail());
    }

    private static void validate(AuthRequest request) {
        if (request == null || blank(request.getEmail()) || blank(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required.");
        }
        if (request.getPassword().length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 4 characters.");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }
}
