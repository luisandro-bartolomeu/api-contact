package com.luisandro.Contactos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luisandro.Contactos.dto.ContactRequestDTO;
import com.luisandro.Contactos.dto.ContactResponseDTO;
import com.luisandro.Contactos.exception.ResourceNotFoundException;
import com.luisandro.Contactos.security.jwt.JwtAuthenticationFilter;
import com.luisandro.Contactos.security.jwt.JwtService;
import com.luisandro.Contactos.security.user.UserDetailsServiceImpl;
import com.luisandro.Contactos.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = ContactController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContactService contactService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ContactResponseDTO contactResponse;
    private ContactRequestDTO contactRequest;

    @BeforeEach
    void setUp() {
        contactResponse = new ContactResponseDTO(1L, UUID.randomUUID(), "Joao Silva", "joao@email.com", "923456789", "Rua A, 123");
        contactRequest = new ContactRequestDTO("Joao Silva", "joao@email.com", "923456789", "Rua A, 123");
    }

    @Test
    void createContactShouldReturnCreatedContact() throws Exception {
        when(contactService.createContact(any(ContactRequestDTO.class))).thenReturn(contactResponse);

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Joao Silva"));
    }

    @Test
    void createContactShouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        ContactRequestDTO invalidRequest = new ContactRequestDTO("", "email-invalido", "123", "Rua A, 123");

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.phone").exists());
    }

    @Test
    void getAllContactsShouldReturnListOfContacts() throws Exception {
        when(contactService.getAllContacts()).thenReturn(List.of(contactResponse));

        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Joao Silva"));
    }

    @Test
    void updateContactShouldReturnUpdatedContact() throws Exception {
        ContactResponseDTO updated = new ContactResponseDTO(1L, UUID.randomUUID(), "Joao Santos", "joao.santos@email.com", "923456780", "Rua B, 456");
        when(contactService.updateContact(any(Long.class), any(ContactRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Joao Santos"));
    }

    @Test
    void getContactByIdShouldReturnContactWhenExists() throws Exception {
        when(contactService.getContactById(1L)).thenReturn(contactResponse);

        mockMvc.perform(get("/api/contacts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Joao Silva"));
    }

    @Test
    void getContactByIdShouldReturnNotFoundWhenMissing() throws Exception {
        when(contactService.getContactById(99L)).thenThrow(new ResourceNotFoundException("Contact not found with id: 99"));

        mockMvc.perform(get("/api/contacts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Contact not found with id: 99"));
    }

    @Test
    void deleteContactShouldReturnNoContentWhenSuccessful() throws Exception {
        doNothing().when(contactService).deleteContact(1L);

        mockMvc.perform(delete("/api/contacts/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteContactShouldReturnNotFoundWhenMissing() throws Exception {
        doThrow(new ResourceNotFoundException("Contact not found with id: 99")).when(contactService).deleteContact(99L);

        mockMvc.perform(delete("/api/contacts/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchContactsByNameShouldReturnMatchingContacts() throws Exception {
        when(contactService.searchContactsByName("Joao")).thenReturn(List.of(contactResponse));

        mockMvc.perform(get("/api/contacts/search").param("name", "Joao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("joao@email.com"));
    }

    @Test
    void getContactByEmailShouldReturnContactWhenExists() throws Exception {
        when(contactService.getContactByEmail("joao@email.com")).thenReturn(contactResponse);

        mockMvc.perform(get("/api/contacts/email/joao@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }
}
