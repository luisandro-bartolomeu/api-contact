package com.luisandro.Contactos.service;

import com.luisandro.Contactos.model.Contact;
import com.luisandro.Contactos.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    public ContactRepository contactRepository;

    public Contact createContact(Contact contact){
        return contactRepository.save(contact);
    }

    public List<Contact> getAllContacts(){
        return contactRepository.findAll();
    }

    public Optional<Contact> getContactById(Long id){
        return contactRepository.findById(id);
    }

    public Optional<Contact> updateContact(Long id, Contact contact){
        return contactRepository.update(id, contact);
    }

    public boolean deleteContact(Long id){
        return contactRepository.deleteById(id);
    }
}
