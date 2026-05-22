package com.luisandro.Contactos.controller;



import com.luisandro.Contactos.dto.ContactRequestDTO;
import com.luisandro.Contactos.dto.ContactResponseDTO;
import com.luisandro.Contactos.security.jwt.JwtAuthenticationFilter;
import com.luisandro.Contactos.security.jwt.JwtService;
import com.luisandro.Contactos.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ContactController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        })
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;

    @Autowired
    private ObjectMapper objectMapper;

    private ContactResponseDTO contactResponse;
    private ContactRequestDTO contactRequest;

    @BeforeEach
    void setUp() {
        contactResponse = new ContactResponseDTO(1L, UUID.randomUUID(), "João Silva", "joao@email.com", "11999999999", "Rua A, 123");
        contactRequest = new ContactRequestDTO("João Silva", "joao@email.com", "11999999999", "Rua A, 123");
    }

    @Test
    void createContact_ShouldReturnCreatedContact() throws Exception {
        when(contactService.createContact(any(ContactRequestDTO.class))).thenReturn(contactResponse);

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João Silva"));
    }

    @Test
    void getAllContacts_ShouldReturnListOfContacts() throws Exception {
        when(contactService.getAllContacts()).thenReturn(List.of(contactResponse));

        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("João Silva"));
    }

    @Test
    void getContactById_ShouldReturnContact_WhenExists() throws Exception {
        when(contactService.getContactById(1L)).thenReturn(contactResponse);

        mockMvc.perform(get("/api/contacts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva"));
    }

    @Test
    void deleteContact_ShouldReturnNoContent_WhenSuccessful() throws Exception {
        doNothing().when(contactService).deleteContact(1L);

        mockMvc.perform(delete("/api/contacts/1"))
                .andExpect(status().isNoContent());
    }
}