package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Mono<String> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {
                    Key key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
                    return Jwts.builder()
                            .setSubject(user.getUsername())
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                            .signWith(key)
                            .compact();
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid username or password")));
    }

    public Mono<User> register(String username, String password, String role) {
        return userRepository.findByUsername(username)
                .flatMap(existingUser -> Mono.<User>error(new IllegalArgumentException("El nombre de usuario ya está en uso")))
                .switchIfEmpty(validateRole(role)
                        .flatMap(userRole -> {
                            User newUser = User.builder()
                                    .username(username)
                                    .password(passwordEncoder.encode(password))
                                    .role(userRole)
                                    .build();
                            return userRepository.save(newUser);
                        }));
    }

    private Mono<Role> validateRole(String role) {
        return Mono.fromSupplier(() -> {
            if (role == null) {
                return Role.USER;
            }
            return Role.valueOf(role.toUpperCase());
        }).onErrorResume(e -> Mono.error(new IllegalArgumentException("Rol inválido. Los roles válidos son: USER, ADMIN.")));
    }

}
