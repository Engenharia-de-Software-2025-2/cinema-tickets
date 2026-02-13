package com.es.cinema.tickets.service;

import com.es.cinema.tickets.dto.AuthResponse;
import com.es.cinema.tickets.dto.LoginRequest;
import com.es.cinema.tickets.dto.LoginResponse;
import com.es.cinema.tickets.dto.RegisterRequest;
import com.es.cinema.tickets.model.User;
import com.es.cinema.tickets.repository.UserRepository;
import com.es.cinema.tickets.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        var user = User.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .role(request.getRole())
                .cpf(request.getCpf())
                .celular(request.getCelular())
                .build();

        userRepository.save(user);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        var jwtToken = jwtService.generateToken(claims, user);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public LoginResponse login(LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        User user = (User) auth.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        String jwtToken = jwtService.generateToken(claims, user);

        return LoginResponse.builder()
                .accessToken(jwtToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationSeconds())
                .build();
    }
}
