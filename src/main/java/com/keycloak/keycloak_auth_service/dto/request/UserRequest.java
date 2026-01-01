package com.keycloak.keycloak_auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UserRequest {

    @NotBlank(message = "Kullanıcı adı zorunludur")
    private String userName;
    @NotBlank(message = "Şifre zorunludur")
    private String password;
    @NotBlank(message = "Ad zorunludur")
    private String firstName;
    @NotBlank(message = "Soyad zorunludur")
    private String lastName;
    @NotBlank(message = "Email zorunludur")
    private String email;
    
    // Optional: role alanı
    private String role;

    // Getters
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    // Setters
    public void setUserName(String userName) { this.userName = userName; }
    public void setPassword(String password) { this.password = password; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
}
