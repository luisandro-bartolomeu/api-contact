package com.luisandro.Contactos.service;

import com.luisandro.Contactos.dto.ContactRequestDTO;
import com.luisandro.Contactos.dto.ContactResponseDTO;
import com.luisandro.Contactos.exception.ResourceNotFoundException;
import com.luisandro.Contactos.model.Contact;
import com.luisandro.Contactos.repository.ContactRepository;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    @Override
    public ContactResponseDTO createContact(ContactRequestDTO contactRequest) {
        // Verificar se email já existe
        if (contactRepository.existsByEmail(contactRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + contactRequest.getEmail());
        }

        Contact contact = mapToEntity(contactRequest);
        Contact savedContact = contactRepository.save(contact);
        return mapToDTO(savedContact);
    }

    @Override
    public List<ContactResponseDTO> getAllContacts() {
        return contactRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContactResponseDTO getContactById(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + id));
        return mapToDTO(contact);
    }

    @Override
    public ContactResponseDTO updateContact(Long id, ContactRequestDTO contactRequest) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + id));

        // Verificar se email já existe para outro contato
        if (!contact.getEmail().equals(contactRequest.getEmail()) &&
                contactRepository.existsByEmail(contactRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + contactRequest.getEmail());
        }

        contact.setName(contactRequest.getName());
        contact.setEmail(contactRequest.getEmail());
        contact.setPhone(contactRequest.getPhone());
        contact.setAddress(contactRequest.getAddress());

        Contact updatedContact = contactRepository.save(contact);
        return mapToDTO(updatedContact);
    }

    @Override
    public void deleteContact(Long id) {
        if (!contactRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contact not found with id: " + id);
        }
        contactRepository.deleteById(id);
    }

    @Override
    public List<ContactResponseDTO> searchContactsByName(String name) {
        return contactRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContactResponseDTO getContactByEmail(String email) {
        Contact contact = contactRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with email: " + email));
        return mapToDTO(contact);
    }

    private Contact mapToEntity(ContactRequestDTO dto) {
        Contact contact = new Contact();
        contact.setName(dto.getName());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contact.setAddress(dto.getAddress());
        return contact;
    }

    private ContactResponseDTO mapToDTO(Contact contact) {
        return new ContactResponseDTO(
                contact.getId(),
                contact.getUuid(),
                contact.getName(),
                contact.getEmail(),
                contact.getPhone(),
                contact.getAddress()
        );
    }
}
