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
import static org.mockito.Mockito.*;

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
        contact = new Contact(1L, UUID.randomUUID(),"João Silva", "joao@email.com", "11999999999", "Rua A, 123");
        contactRequest = new ContactRequestDTO("João Silva", "joao@email.com", "11999999999", "Rua A, 123");
    }

    @Test
    void createContact_ShouldReturnContactResponseDTO_WhenSuccessful() {
        when(contactRepository.existsByEmail(anyString())).thenReturn(false);
        when(contactRepository.save(any(Contact.class))).thenReturn(contact);

        ContactResponseDTO result = contactService.createContact(contactRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("João Silva");
        assertThat(result.getEmail()).isEqualTo("joao@email.com");
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void createContact_ShouldThrowException_WhenEmailExists() {
        when(contactRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> contactService.createContact(contactRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exists");

        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void getAllContacts_ShouldReturnListOfContactResponseDTO() {
        when(contactRepository.findAll()).thenReturn(List.of(contact));

        List<ContactResponseDTO> result = contactService.getAllContacts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("João Silva");
        verify(contactRepository).findAll();
    }

    @Test
    void getContactById_ShouldReturnContactResponseDTO_WhenContactExists() {
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));

        ContactResponseDTO result = contactService.getContactById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(contactRepository).findById(1L);
    }

    @Test
    void getContactById_ShouldThrowException_WhenContactNotFound() {
        when(contactRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactService.getContactById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Contact not found with id: 99");
    }

    @Test
    void updateContact_ShouldReturnUpdatedContact_WhenSuccessful() {
        ContactRequestDTO updateRequest = new ContactRequestDTO(
                "João Santos", "joao.santos@email.com", "11988888888", "Rua B, 456");

        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        when(contactRepository.existsByEmail(anyString())).thenReturn(false);
        when(contactRepository.save(any(Contact.class))).thenAnswer(inv -> {
            Contact c = inv.getArgument(0);
            c.setName(updateRequest.getName());
            c.setEmail(updateRequest.getEmail());
            c.setPhone(updateRequest.getPhone());
            c.setAddress(updateRequest.getAddress());
            return c;
        });

        ContactResponseDTO result = contactService.updateContact(1L, updateRequest);

        assertThat(result.getName()).isEqualTo("João Santos");
        assertThat(result.getEmail()).isEqualTo("joao.santos@email.com");
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void deleteContact_ShouldDelete_WhenContactExists() {
        when(contactRepository.existsById(1L)).thenReturn(true);
        doNothing().when(contactRepository).deleteById(1L);

        contactService.deleteContact(1L);

        verify(contactRepository).deleteById(1L);
    }

    @Test
    void deleteContact_ShouldThrowException_WhenContactNotFound() {
        when(contactRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> contactService.deleteContact(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(contactRepository, never()).deleteById(anyLong());
    }

    @Test
    void searchContactsByName_ShouldReturnMatchingContacts() {
        when(contactRepository.findByNameContainingIgnoreCase("João")).thenReturn(List.of(contact));

        List<ContactResponseDTO> result = contactService.searchContactsByName("João");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("João");
        verify(contactRepository).findByNameContainingIgnoreCase("João");
    }
}