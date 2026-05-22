package com.luisandro.Contactos.security.config;



import com.luisandro.Contactos.security.jwt.JwtAuthenticationFilter;
import com.luisandro.Contactos.security.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // Permite anotações como @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 1. Configura QUAL tipo de autenticação usar para cada endpoint
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (não necessário para APIs REST)
                .csrf(csrf -> csrf.disable())

                // Configura política de sessão: STATELESS (não guarda sessão no servidor)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configura quais endpoints são públicos e quais exigem autenticação
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (não precisam de autenticação)
                        .requestMatchers("/auth/**").permitAll()           // Login, registro
                        .requestMatchers("/h2-console/**").permitAll()     // Console do banco
                        .requestMatchers("/swagger-ui/**").permitAll()     // Documentação
                        .requestMatchers("/api-docs/**").permitAll()       // Documentação

                        // Endpoints que exigem autenticação (qualquer usuário logado)
                        .requestMatchers("/api/contacts/**").authenticated()

                        // Endpoints específicos para ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Qualquer outra requisição precisa autenticação
                        .anyRequest().authenticated()
                )

                // Configura Basic Authentication (opcional - HABILITADO)
                .httpBasic(httpBasic -> {})

                // Configura OAuth2 Login
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/oauth2/success", true)
                );

        // Adiciona o filtro JWT ANTES do filtro padrão do UsernamePassword
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 2. Configura como o Spring Security busca o usuário
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);  // Onde buscar usuários
        authProvider.setPasswordEncoder(passwordEncoder());      // Como comparar senhas
        return authProvider;
    }

    // 3. Gerencia a autenticação (quem está tentando logar)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 4. Criptografa senhas (BCrypt é seguro e lento de propósito)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}