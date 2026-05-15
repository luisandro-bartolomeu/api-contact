package com.luisandro.Contactos.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.luisandro.Contactos.model.Contact;
import org.springframework.stereotype.Repository;

@Repository
public class ContactRepository{
    private List<Contact> contacts = new ArrayList<>();
    private AtomicLong idCounter = new AtomicLong(1);

    public Contact save(Contact contact){
        contact.setId(idCounter.getAndIncrement());
        contacts.add(contact);
        return contact;
    }

    public List<Contact> findAll(){
        return new ArrayList<>(contacts);
    }

    public Optional<Contact> findById(Long id){
        return contacts.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    public Optional<Contact> update(Long id, Contact updateContact){
        return findById(id).map(existingContact -> {
                existingContact.setName(updateContact.getName());
                existingContact.setAddress(updateContact.getAddress());
                existingContact.setEmail(updateContact.getEmail());
                existingContact.setPhone(updateContact.getPhone());
                return existingContact;
        });
    }

    public boolean deleteById(Long id){
        return contacts.removeIf(c -> c.getId().equals(id));
    }
}
