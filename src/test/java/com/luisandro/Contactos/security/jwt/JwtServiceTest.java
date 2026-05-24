package com.luisandro.Contactos.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "0123456789012345678901234567890123456789");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
    }

    @Test
    void generateTokenShouldProduceValidJwt() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "joao",
                        "encoded",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        String token = jwtService.generateToken(authentication);

        assertThat(token).isNotBlank();
        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(jwtService.getUsernameFromToken(token)).isEqualTo("joao");
    }

    @Test
    void validateTokenShouldRejectInvalidJwt() {
        assertThat(jwtService.validateToken("invalid-token")).isFalse();
    }

    @Test
    void validateTokenWithUserDetailsShouldMatchUsername() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "joao",
                        "encoded",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        String token = jwtService.generateToken(principal);

        assertThat(jwtService.validateToken(token, principal)).isTrue();
        assertThat(jwtService.validateToken(
                token,
                new org.springframework.security.core.userdetails.User("maria", "encoded", List.of())
        )).isFalse();
    }
}
