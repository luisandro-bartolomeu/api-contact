package com.luisandro.Contactos.repository;


import java.util.List;
import java.util.Optional;

import com.luisandro.Contactos.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
  Optional<Contact> findByEmail(String email);
  List<Contact> findByNameContainingIgnoreCase(String name);
  Optional<Contact> findByPhone(String phone);
  @Query("SELECT c FROM Contact c WHERE c.email LIKE %:domain%")
  List<Contact> findByEmailDomain(@Param("domain") String domain);
  boolean existsByEmail(String email);
}
