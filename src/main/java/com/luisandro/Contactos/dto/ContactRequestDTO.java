package com.luisandro.Contactos.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequestDTO {
    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 character")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone is mandatory")
    @Pattern(regexp = "^(?:\\+244\\s?)?(?:9\\d{2}[-\\s]?\\d{3}[-\\s]?\\d{3})$", message = "Phone invalid format. Use 923456789, 923-456-789, +244923456789 or +244 923 456 789")
    private String phone;

    @Size(max = 255)
    private String address;
}
