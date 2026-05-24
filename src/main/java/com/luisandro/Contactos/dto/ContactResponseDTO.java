package com.luisandro.Contactos.dto;

import java.util.UUID;

public class ContactResponseDTO {
    private Long id;
    private UUID uuid;
    private String name;
    private String email;
    private String phone;
    private String address;

    public ContactResponseDTO() {
    }

    public ContactResponseDTO(Long id, UUID uuid, String name, String email, String phone, String address) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
