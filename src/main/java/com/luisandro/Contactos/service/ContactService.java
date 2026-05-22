package com.luisandro.Contactos.service;

import com.luisandro.Contactos.dto.ContactRequestDTO;
import com.luisandro.Contactos.dto.ContactResponseDTO;
import com.luisandro.Contactos.model.Contact;
import com.luisandro.Contactos.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface ContactService {


    ContactResponseDTO createContact(ContactRequestDTO contactRequest);
    List<ContactResponseDTO> getAllContacts();
    ContactResponseDTO getContactById(Long id);
    ContactResponseDTO updateContact(Long id, ContactRequestDTO contactRequest);
    void deleteContact(Long id);
    List<ContactResponseDTO> searchContactsByName(String name);
    ContactResponseDTO getContactByEmail(String email);
}
