package com.luisandro.Contactos.services;

import com.luisandro.Contactos.dto.ContactRequestDTO;
import com.luisandro.Contactos.dto.ContactResponseDTO;
import com.luisandro.Contactos.exception.ResourceNotFoundException;
import com.luisandro.Contactos.model.Contact;
import com.luisandro.Contactos.repository.ContactRepository;
import com.luisandro.Contactos.service.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    private Contact contact;
    private ContactRequestDTO contactRequest;

    @BeforeEach
    void setUp() {
        contact = new Contact(1L, UUID.randomUUID(), "Joao Silva", "joao@email.com", "923456789", "Rua A, 123");
        contactRequest = new ContactRequestDTO("Joao Silva", "joao@email.com", "923456789", "Rua A, 123");
    }

    @Test
    void createContactShouldReturnContactResponseDtoWhenSuccessful() {
        when(contactRepository.existsByEmail(anyString())).thenReturn(false);
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);

        ContactResponseDTO result = contactService.createContact(contactRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Joao Silva");
        assertThat(result.getEmail()).isEqualTo("joao@email.com");
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void createContactShouldThrowExceptionWhenEmailExists() {
        when(contactRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> contactService.createContact(contactRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exists");

        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void getAllContactsShouldReturnListOfContactResponseDto() {
        when(contactRepository.findAll()).thenReturn(List.of(contact));

        List<ContactResponseDTO> result = contactService.getAllContacts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Joao Silva");
    }

    @Test
    void getContactByIdShouldReturnContactResponseDtoWhenContactExists() {
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));

        ContactResponseDTO result = contactService.getContactById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getContactByIdShouldThrowExceptionWhenContactNotFound() {
        when(contactRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactService.getContactById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Contact not found with id: 99");
    }

    @Test
    void updateContactShouldReturnUpdatedContactWhenSuccessful() {
        ContactRequestDTO updateRequest = new ContactRequestDTO(
                "Joao Santos", "joao.santos@email.com", "923456780", "Rua B, 456");

        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        when(contactRepository.existsByEmail(updateRequest.getEmail())).thenReturn(false);
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContactResponseDTO result = contactService.updateContact(1L, updateRequest);

        assertThat(result.getName()).isEqualTo("Joao Santos");
        assertThat(result.getEmail()).isEqualTo("joao.santos@email.com");
        assertThat(result.getPhone()).isEqualTo("923456780");
    }

    @Test
    void updateContactShouldThrowWhenContactDoesNotExist() {
        when(contactRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactService.updateContact(1L, contactRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Contact not found with id: 1");
    }

    @Test
    void updateContactShouldThrowWhenEmailBelongsToAnotherContact() {
        ContactRequestDTO updateRequest = new ContactRequestDTO(
                "Joao Santos", "duplicado@email.com", "923456780", "Rua B, 456");

        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        when(contactRepository.existsByEmail(updateRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> contactService.updateContact(1L, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    void deleteContactShouldDeleteWhenContactExists() {
        when(contactRepository.existsById(1L)).thenReturn(true);
        doNothing().when(contactRepository).deleteById(1L);

        contactService.deleteContact(1L);

        verify(contactRepository).deleteById(1L);
    }

    @Test
    void deleteContactShouldThrowExceptionWhenContactNotFound() {
        when(contactRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> contactService.deleteContact(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Contact not found with id: 99");
    }

    @Test
    void searchContactsByNameShouldReturnMatchingContacts() {
        when(contactRepository.findByNameContainingIgnoreCase("Joao")).thenReturn(List.of(contact));

        List<ContactResponseDTO> result = contactService.searchContactsByName("Joao");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Joao");
    }

    @Test
    void getContactByEmailShouldReturnContactWhenFound() {
        when(contactRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(contact));

        ContactResponseDTO result = contactService.getContactByEmail("joao@email.com");

        assertThat(result.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void getContactByEmailShouldThrowWhenContactDoesNotExist() {
        when(contactRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactService.getContactByEmail("missing@email.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Contact not found with email: missing@email.com");
    }
}
