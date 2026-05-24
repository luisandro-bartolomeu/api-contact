package com.luisandro.Contactos.security.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsernameShouldReturnMappedUser() {
        User user = new User();
        user.setUsername("joao");
        user.setEmail("joao@email.com");
        user.setPassword("encoded");
        user.setRoles(Set.of(Role.ROLE_USER));

        when(userRepository.findByUsername("joao")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("joao");

        assertThat(result.getUsername()).isEqualTo("joao");
        assertThat(result.getAuthorities()).extracting("authority").contains("ROLE_USER");
    }

    @Test
    void loadUserByUsernameShouldFallbackToEmail() {
        User user = new User();
        user.setUsername("joao");
        user.setEmail("joao@email.com");
        user.setPassword("encoded");
        user.setRoles(Set.of(Role.ROLE_USER));

        when(userRepository.findByUsername("joao@email.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("joao@email.com");

        assertThat(result.getUsername()).isEqualTo("joao");
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: missing");
    }
}
