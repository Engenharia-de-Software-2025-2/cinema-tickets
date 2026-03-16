package com.es.cinema.tickets.application.service;

import com.es.cinema.tickets.persistence.enums.Role;
import com.es.cinema.tickets.persistence.entity.User;
import com.es.cinema.tickets.security.AuthUserDetails;
import com.es.cinema.tickets.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
class JwtServiceTest {

    private JwtService jwtService;
    private AuthUserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        String testSecret = java.util.Base64.getEncoder()
                .encodeToString("12345678901234567890123456789012".getBytes());

        java.lang.reflect.Field secretField = JwtService.class.getDeclaredField("secretKey");
        secretField.setAccessible(true);
        secretField.set(jwtService, testSecret);

        java.lang.reflect.Field expField = JwtService.class.getDeclaredField("jwtExpirationMs");
        expField.setAccessible(true);
        expField.set(jwtService, 1000 * 60 * 60L);

        User user = User.builder()
                .id(1L)
                .email("luis@email.com")
                .passwordHash("encoded")
                .role(Role.USER)
                .nome("Luis")
                .cpf("12345678901")
                .build();

        userDetails = new AuthUserDetails(user);
    }

    @Test
    void shouldGenerateTokenWithoutExtraClaims() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(userDetails.getUsername(), jwtService.extractUsername(token));
    }

    @Test
    void shouldGenerateTokenWithExtraClaims() {
        Map<String, Object> claims = Map.of("role", "USER");
        String token = jwtService.generateToken(claims, userDetails);
        assertNotNull(token);
        assertEquals(userDetails.getUsername(), jwtService.extractUsername(token));
        assertEquals("USER", jwtService.extractClaim(token, claimsMap -> claimsMap.get("role")));
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldInvalidateTokenForDifferentUser() {
        String token = jwtService.generateToken(userDetails);

        User fakeUser = User.builder()
                .id(2L)
                .email("other@email.com")
                .passwordHash("encoded")
                .role(Role.USER)
                .nome("Other")
                .cpf("98765432100")
                .build();
        AuthUserDetails otherDetails = new AuthUserDetails(fakeUser);

        assertFalse(jwtService.isTokenValid(token, otherDetails));
    }

    @Test
    void shouldReturnExpirationInSeconds() {
        assertEquals(3600, jwtService.getExpirationSeconds());
    }
}