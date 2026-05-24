package com.luisandro.Contactos.security.auth;

import com.luisandro.Contactos.security.jwt.JwtService;
import com.luisandro.Contactos.security.user.Role;
import com.luisandro.Contactos.security.user.User;
import com.luisandro.Contactos.security.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerUserShouldPersistEncodedUser() {
        when(userRepository.existsByUsername("joao")).thenReturn(false);
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.registerUser("joao", "joao@email.com", "123456", "Joao Silva");

        assertThat(result.getUsername()).isEqualTo("joao");
        assertThat(result.getPassword()).isEqualTo("encoded");
        assertThat(result.getRoles()).contains(Role.ROLE_USER);
    }

    @Test
    void registerUserShouldRejectDuplicateUsername() {
        when(userRepository.existsByUsername("joao")).thenReturn(true);

        assertThatThrownBy(() -> authService.registerUser("joao", "joao@email.com", "123456", "Joao Silva"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username already registered");
    }

    @Test
    void registerUserShouldRejectDuplicateEmail() {
        when(userRepository.existsByUsername("joao")).thenReturn(false);
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registerUser("joao", "joao@email.com", "123456", "Joao Silva"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void authenticateAndGetTokenShouldReturnJwtPayload() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "joao",
                        "encoded",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        User persisted = new User();
        persisted.setUsername("joao");
        persisted.setEmail("joao@email.com");
        persisted.setFullName("Joao Silva");
        persisted.setRoles(Set.of(Role.ROLE_USER));

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtService.generateToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(persisted));

        LoginResponse response = authService.authenticateAndGetToken("joao", "123456");

        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("joao");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);
        verify(jwtService).generateToken(authentication);
    }

    @Test
    void authenticateAndGetTokenShouldFindUserByEmailWhenNeeded() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "joao",
                        "encoded",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        User persisted = new User();
        persisted.setUsername("joao");
        persisted.setEmail("joao@email.com");
        persisted.setFullName("Joao Silva");

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtService.generateToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByUsername("joao@email.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(persisted));

        LoginResponse response = authService.authenticateAndGetToken("joao@email.com", "123456");

        assertThat(response.getEmail()).isEqualTo("joao@email.com");
    }
}
