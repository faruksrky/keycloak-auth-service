package com.keycloak.keycloak_auth_service.dto.response;

public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private Boolean enabled;
    private Boolean emailVerified;
    private String role;

    // Getters
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getUserName() { return userName; }
    public Boolean getEnabled() { return enabled; }
    public Boolean getEmailVerified() { return emailVerified; }
    public String getRole() { return role; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    public void setRole(String role) { this.role = role; }
}
