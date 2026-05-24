package com.luisandro.Contactos.security.auth;

public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String username;
    private String email;
    private String fullName;

    public LoginResponse() {
    }

    public LoginResponse(String accessToken, String tokenType, String username, String email, String fullName) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    public LoginResponse(String accessToken, String username, String email, String fullName) {
        this.accessToken = accessToken;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
