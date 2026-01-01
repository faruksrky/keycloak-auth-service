package com.keycloak.keycloak_auth_service.dto.response;

public class UserNameResponse {
    private String id;
    private String username;

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
}
