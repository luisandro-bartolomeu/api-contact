package com.luisandro.Contactos.security.config;

import com.luisandro.Contactos.security.user.Role;
import com.luisandro.Contactos.security.user.User;
import com.luisandro.Contactos.security.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapAdminConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapAdminConfig(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner bootstrapAdmin(
            @Value("${app.bootstrap-admin.enabled:true}") boolean enabled,
            @Value("${app.bootstrap-admin.username:admin}") String username,
            @Value("${app.bootstrap-admin.email:admin@contactos.local}") String email,
            @Value("${app.bootstrap-admin.password:admin123}") String password,
            @Value("${app.bootstrap-admin.full-name:Default Admin}") String fullName) {
        return args -> {
            if (!enabled || userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
                return;
            }

            User admin = new User();
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setFullName(fullName);
            admin.getRoles().add(Role.ROLE_ADMIN);
            admin.getRoles().add(Role.ROLE_USER);
            userRepository.save(admin);
        };
    }
}
