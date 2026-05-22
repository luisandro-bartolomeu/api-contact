package com.luisandro.Contactos.security.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String username;
    private String email;
    private String fullName;

    public LoginResponse(String accessToken, String username, String email, String fullName) {
        this.accessToken = accessToken;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }
}