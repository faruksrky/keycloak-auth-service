package com.keycloak.keycloak_auth_service.controller;

import com.keycloak.keycloak_auth_service.dto.response.TokenDto;
import com.keycloak.keycloak_auth_service.dto.response.UserResponse;
import com.keycloak.keycloak_auth_service.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/keycloak")
public class LoginController {

    private final KeycloakService keycloakService;

    public LoginController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @GetMapping("/userInfo")
    public ResponseEntity<?> getTokenInfo(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Eksik veya hatalı Authorization header");
            }
            String accessToken = authorizationHeader.replace("Bearer ", "").trim();
            UserResponse user = keycloakService.extractUserInfo(accessToken);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token çözümlenemedi: " + e.getMessage());
        }
    }

    @PostMapping("/getToken")
    public ResponseEntity<TokenDto> getToken(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        return ResponseEntity.ok(keycloakService.getToken(username, password));
    }

    @PostMapping("/getTokenAdmin")
    public ResponseEntity<TokenDto> getTokenAdmin(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        return ResponseEntity.ok(keycloakService.getTokenAdmin(username, password));
    }
}
