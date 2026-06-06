package com.careflow.hms.dto;

public class AuthResponse {
    private String token;
    private String refreshToken;
    private String role;
    private String username;

    public AuthResponse() {
    }

    public AuthResponse(String token, String refreshToken, String role, String username) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.role = role;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
