package com.luisandro.Contactos.security.config;

import com.luisandro.Contactos.security.user.User;
import com.luisandro.Contactos.security.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootstrapAdminConfigTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BootstrapAdminConfig bootstrapAdminConfig;

    @Test
    void bootstrapAdminShouldCreateAdminWhenMissing() throws Exception {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.existsByEmail("admin@contactos.local")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("encoded");

        CommandLineRunner runner = bootstrapAdminConfig.bootstrapAdmin(
                true, "admin", "admin@contactos.local", "admin123", "Default Admin");
        runner.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRoles()).extracting(Enum::name).contains("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void bootstrapAdminShouldDoNothingWhenDisabled() throws Exception {
        CommandLineRunner runner = bootstrapAdminConfig.bootstrapAdmin(
                false, "admin", "admin@contactos.local", "admin123", "Default Admin");
        runner.run();

        verify(userRepository, never()).save(any(User.class));
    }
}
