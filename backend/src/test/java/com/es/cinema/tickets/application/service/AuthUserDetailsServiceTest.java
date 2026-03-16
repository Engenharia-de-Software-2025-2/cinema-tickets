package com.es.cinema.tickets.application.service;

import com.es.cinema.tickets.persistence.entity.User;
import com.es.cinema.tickets.persistence.enums.Role;
import com.es.cinema.tickets.persistence.repository.UserRepository;
import com.es.cinema.tickets.security.AuthUserDetails;
import com.es.cinema.tickets.security.AuthUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthUserDetailsServiceTest {

    private AuthUserDetailsService authUserDetailsService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authUserDetailsService = new AuthUserDetailsService(userRepository);
    }

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        User user = User.builder()
                .id(1L)
                .email("luis@email.com")
                .passwordHash("encoded")
                .role(Role.USER)
                .nome("Luis")
                .cpf("12345678901")
                .build();

        when(userRepository.findByEmail("luis@email.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = authUserDetailsService.loadUserByUsername("luis@email.com");

        assertNotNull(userDetails);
        assertInstanceOf(AuthUserDetails.class, userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPasswordHash(), userDetails.getPassword());
        assertEquals(user.getRole(), ((AuthUserDetails) userDetails).getRole());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                authUserDetailsService.loadUserByUsername("missing@email.com")
        );
    }
}