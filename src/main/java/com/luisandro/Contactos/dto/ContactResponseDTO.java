package com.luisandro.Contactos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponseDTO {
       private Long Id;
       private UUID uuid;
       private String name;
       private String email;
       private String phone;
       private String address;
}
