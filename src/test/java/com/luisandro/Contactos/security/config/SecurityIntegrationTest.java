package com.luisandro.Contactos.security.config;

import com.luisandro.Contactos.service.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "APP_BOOTSTRAP_ADMIN_USERNAME=admin",
        "APP_BOOTSTRAP_ADMIN_EMAIL=admin@contactos.local",
        "APP_BOOTSTRAP_ADMIN_PASSWORD=admin123",
        "APP_BOOTSTRAP_ADMIN_ENABLED=true"
})
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;

    @Test
    void shouldRejectProtectedEndpointWithoutCredentials() throws Exception {
        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowPublicAuthEndpoint() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldAllowBasicAuthenticationOnProtectedEndpoint() throws Exception {
        when(contactService.getAllContacts()).thenReturn(List.of());

        mockMvc.perform(get("/api/contacts").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk());
    }
}
