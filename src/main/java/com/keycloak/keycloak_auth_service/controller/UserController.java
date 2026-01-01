package com.keycloak.keycloak_auth_service.controller;

import com.keycloak.keycloak_auth_service.dto.request.UserRequest;
import com.keycloak.keycloak_auth_service.dto.response.UserNameResponse;
import com.keycloak.keycloak_auth_service.dto.response.UserResponse;
import jakarta.ws.rs.WebApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.keycloak.keycloak_auth_service.service.UserService;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}/send-verification-email")
    public ResponseEntity<?> sendVerificationEmail(@PathVariable String id) {

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {

        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PutMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String username) {

        userService.forgotPassword(username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

        @GetMapping("/userInfo")
    public ResponseEntity<?> getUserInfoByToken(@RequestBody String accessToken) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserInfoByToken(accessToken));
    }

    @GetMapping("/usernames")
    public ResponseEntity<List<UserNameResponse>> getAllUsernames() {
        List<UserNameResponse> usernames = userService.getAllUsernames();
        return ResponseEntity.ok(usernames);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listUsers() {
        try {
            var users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (WebApplicationException ex) {
            // JAX-RS hatası: Keycloak’tan gelen HTTP status’u geçir
            int status = ex.getResponse() != null ? ex.getResponse().getStatus() : 500;
            return ResponseEntity.status(status).body(Map.of(
                    "error", "Keycloak error",
                    "status", status
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal error",
                    "message", e.getMessage()
            ));
        }
    }

}
