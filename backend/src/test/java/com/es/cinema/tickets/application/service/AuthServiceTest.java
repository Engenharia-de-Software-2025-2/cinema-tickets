package com.es.cinema.tickets.application.service;

import com.es.cinema.tickets.exception.business.CpfAlreadyRegisteredException;
import com.es.cinema.tickets.exception.business.EmailAlreadyRegisteredException;
import com.es.cinema.tickets.exception.business.InvalidCredentialsException;
import com.es.cinema.tickets.persistence.entity.User;
import com.es.cinema.tickets.persistence.enums.Role;
import com.es.cinema.tickets.persistence.repository.UserRepository;
import com.es.cinema.tickets.security.AuthUserDetails;
import com.es.cinema.tickets.security.JwtService;
import com.es.cinema.tickets.web.dto.request.LoginRequest;
import com.es.cinema.tickets.web.dto.request.RegisterRequest;
import com.es.cinema.tickets.web.dto.response.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);

        authService = new AuthService(userRepository, passwordEncoder, jwtService, authenticationManager);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setNome("Luis");
        request.setEmail("luis@email.com");
        request.setPassword("123456");
        request.setCpf("12345678901");
        request.setCelular("999999999");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByCpf(request.getCpf())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(request.getNome(), savedUser.getNome());
        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPasswordHash());
        assertEquals(Role.USER, savedUser.getRole());
        assertEquals(request.getCpf(), savedUser.getCpf());
        assertEquals(request.getCelular(), savedUser.getCelular());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyRegistered() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("luis@email.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyRegisteredException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCpfAlreadyRegistered() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("luis@email.com");
        request.setCpf("12345678901");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByCpf(request.getCpf())).thenReturn(true);

        assertThrows(CpfAlreadyRegisteredException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("luis@email.com", "123456");

        User user = User.builder()
                .id(1L)
                .email("luis@email.com")
                .passwordHash("encoded")
                .role(Role.USER)
                .nome("Luis")
                .cpf("12345678901")
                .build();

        AuthUserDetails userDetails = new AuthUserDetails(user);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(any(), eq(userDetails))).thenReturn("token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        LoginResponse response = authService.login(request);

        assertEquals("token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
    }

    @Test
    void shouldThrowExceptionWhenInvalidCredentials() {
        LoginRequest request = new LoginRequest("luis@email.com", "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenAnswer(invocation -> {
                    throw new InvalidCredentialsException();
                });

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void shouldThrowExceptionWhenPrincipalIsNotAuthUserDetails() {
        LoginRequest request = new LoginRequest("luis@email.com", "123456");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("not a user details");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        assertThrows(IllegalStateException.class, () -> authService.login(request));
    }
}