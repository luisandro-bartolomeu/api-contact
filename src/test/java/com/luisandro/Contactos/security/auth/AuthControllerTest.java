package com.luisandro.Contactos.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luisandro.Contactos.security.jwt.JwtAuthenticationFilter;
import com.luisandro.Contactos.security.jwt.JwtService;
import com.luisandro.Contactos.security.user.Role;
import com.luisandro.Contactos.security.user.User;
import com.luisandro.Contactos.security.user.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = AuthController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void loginShouldReturnJwtPayload() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("joao");
        request.setPassword("123456");
        when(authService.authenticateAndGetToken("joao", "123456"))
                .thenReturn(new LoginResponse("token", "joao", "joao@email.com", "Joao Silva"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void registerShouldReturnCreatedUserWithoutPasswordField() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("joao");
        request.setEmail("joao@email.com");
        request.setPassword("123456");
        request.setFullName("Joao Silva");

        User user = new User();
        user.setUsername("joao");
        user.setEmail("joao@email.com");
        user.setPassword("encoded");
        user.setFullName("Joao Silva");
        user.setRoles(Set.of(Role.ROLE_USER));

        when(authService.registerUser(
                eq("joao"),
                eq("joao@email.com"),
                eq("123456"),
                eq("Joao Silva")
        )).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("joao"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void loginShouldValidatePayload() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}
