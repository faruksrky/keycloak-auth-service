package com.keycloak.keycloak_auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
}
