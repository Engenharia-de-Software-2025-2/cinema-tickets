package com.es.cinema.tickets.bootstrap;

import com.es.cinema.tickets.persistence.entity.User;
import com.es.cinema.tickets.persistence.enums.Role;
import com.es.cinema.tickets.persistence.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeederTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "encoded-" + invocation.getArgument(0));
    }

    @Test
    void prodAdminSeeder_shouldNotRunIfDisabled() throws Exception {
        ProdAdminSeeder seeder = new ProdAdminSeeder(userRepository, passwordEncoder);

        // desabilita o seeder via reflection
        Field enabledField = ProdAdminSeeder.class.getDeclaredField("enabled");
        enabledField.setAccessible(true);
        enabledField.set(seeder, false);

        seeder.run(mock(ApplicationArguments.class));

        verify(userRepository, never()).save(any());
    }

    @Test
    void prodAdminSeeder_shouldSaveAdminWhenEnabledAndNoAdminExists() throws Exception {
        final var seeder = getProdAdminSeeder();

        // garante que não existe admin
        when(userRepository.existsByRole(Role.ADMIN)).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        seeder.run(mock(ApplicationArguments.class));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals(Role.ADMIN, saved.getRole());
        assertTrue(saved.getPasswordHash().startsWith("encoded-"));
        assertEquals("admin@prod.dev", saved.getEmail());
    }

    private @NonNull ProdAdminSeeder getProdAdminSeeder() throws NoSuchFieldException, IllegalAccessException {
        ProdAdminSeeder seeder = new ProdAdminSeeder(userRepository, passwordEncoder);

        // habilita o seeder
        Field enabledField = ProdAdminSeeder.class.getDeclaredField("enabled");
        enabledField.setAccessible(true);
        enabledField.set(seeder, true);

        // define email, password e celular para não serem null
        Field emailField = ProdAdminSeeder.class.getDeclaredField("email");
        emailField.setAccessible(true);
        emailField.set(seeder, "admin@prod.dev");

        Field passwordField = ProdAdminSeeder.class.getDeclaredField("password");
        passwordField.setAccessible(true);
        passwordField.set(seeder, "123456");

        Field celularField = ProdAdminSeeder.class.getDeclaredField("celular");
        celularField.setAccessible(true);
        celularField.set(seeder, "999999999"); // ou outro número de teste

        return seeder;
    }

    @Test
    void userSeeder_shouldSaveAllUsersThatDoNotExist() {
        UserSeeder seeder = new UserSeeder(userRepository, passwordEncoder);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf(anyString())).thenReturn(false);

        seeder.seed();

        verify(userRepository, atLeast(4)).save(any(User.class)); // admin + 3 users
    }

    @Test
    void userSeeder_shouldSkipExistingUsers() {
        UserSeeder seeder = new UserSeeder(userRepository, passwordEncoder);

        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        when(userRepository.existsByCpf(anyString())).thenReturn(true);

        seeder.seed();

        verify(userRepository, never()).save(any(User.class));
    }
}