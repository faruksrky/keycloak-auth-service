package com.keycloak.keycloak_auth_service.service.impl;

import com.keycloak.keycloak_auth_service.dto.request.UserRequest;
import com.keycloak.keycloak_auth_service.dto.response.UserAdminResponse;
import com.keycloak.keycloak_auth_service.dto.response.UserNameResponse;
import com.keycloak.keycloak_auth_service.dto.response.UserResponse;
import com.keycloak.keycloak_auth_service.service.UserService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.admin.adminClientSecret}")
    private String adminClientSecret;

    private final Keycloak keycloak;

    @Override
    public void createUser(UserRequest userRequest) {
        if (userRequest.getUserName().isEmpty() ||
                userRequest.getEmail().isEmpty() ||
                userRequest.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kullanıcı adı, şifre ve email boş olamaz");
        }

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setFirstName(userRequest.getFirstName());
        userRepresentation.setLastName(userRequest.getLastName());
        userRepresentation.setUsername(userRequest.getUserName());
        userRepresentation.setEmail(userRequest.getEmail());
        userRepresentation.setEmailVerified(false);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userRequest.getPassword());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        userRepresentation.setCredentials(List.of(credentialRepresentation));

        UsersResource usersResource = getUsersResource();
        if (!CollectionUtils.isEmpty(usersResource.list()) &&
                usersResource.list().stream().anyMatch(x -> x.getUsername().equals(userRequest.getUserName()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Kullanıcı zaten mevcut");
        }

        try (Response response = usersResource.create(userRepresentation)) {
            if (!Objects.equals(201, response.getStatus())) {
                throw new RuntimeException("Status code " + response.getStatus());
            }
        }

        // Kullanıcıyı bul ve rol ataması yap
        List<UserRepresentation> userRepresentations = usersResource.searchByUsername(userRequest.getUserName(), true);
        if (!userRepresentations.isEmpty()) {
            UserRepresentation createdUser = userRepresentations.get(0);
            UserResource userResource = usersResource.get(createdUser.getId());
            
            // Role ataması yap
            String roleName = userRequest.getRole() != null && !userRequest.getRole().isEmpty() 
                ? userRequest.getRole() 
                : "USER"; // Default olarak USER rolü
            
            try {
                List<RoleRepresentation> realmRoles = keycloak.realm(realm).roles().list();
                RoleRepresentation assignedRole = realmRoles.stream()
                    .filter(role -> role.getName().equalsIgnoreCase(roleName))
                    .findFirst()
                    .orElse(null);
                
                if (assignedRole != null) {
                    userResource.roles().realmLevel().add(List.of(assignedRole));
                } else {
                    // Role yoksa default USER rolünü ara
                    assignedRole = realmRoles.stream()
                        .filter(role -> role.getName().equalsIgnoreCase("USER"))
                        .findFirst()
                        .orElse(null);
                    
                    if (assignedRole != null) {
                        userResource.roles().realmLevel().add(List.of(assignedRole));
                    }
                }
            } catch (Exception e) {
                // Role ataması başarısız olursa log'la ama hata fırlatma
                System.err.println("Role assignment failed for user: " + userRequest.getUserName());
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void deleteUser(String userId) {
        UsersResource usersResource = getUsersResource();
        usersResource.delete(userId);
    }

    @Override
    public void forgotPassword(String username) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> userRepresentations = usersResource.searchByUsername(username, true);
        UserRepresentation userRepresentation1 = userRepresentations.get(0);
        UserResource userResource = usersResource.get(userRepresentation1.getId());
        userResource.executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }


    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    @Override
    public List<UserNameResponse> getAllUsernames() {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> userRepresentations = usersResource.list();

        return userRepresentations.stream()
                .map(user -> {
                    UserNameResponse response = new UserNameResponse();
                    response.setId(user.getId());
                    response.setUsername(user.getUsername());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getAllUsers() {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> userRepresentations = usersResource.list();

        return userRepresentations.stream()
                .map(user -> {
                    UserResponse response = new UserResponse();
                    response.setId(user.getId());
                    response.setFirstName(user.getFirstName());
                    response.setLastName(user.getLastName());
                    response.setEmail(user.getEmail());
                    response.setUserName(user.getUsername());
                    response.setEnabled(user.isEnabled());
                    response.setEmailVerified(user.isEmailVerified());
                    
                    // Kullanıcının rolünü al
                    try {
                        UserResource userResource = usersResource.get(user.getId());
                        List<RoleRepresentation> userRoles = userResource.roles().realmLevel().listAll();
                        
                        if (!userRoles.isEmpty()) {
                            String roleName = null;
                            boolean isAdmin = false;
                            
                            // Tüm rollerden ADMIN olan var mı kontrol et
                            for (RoleRepresentation role : userRoles) {
                                String name = role.getName();
                                // ADMIN rolünü ara
                                if (name.equalsIgnoreCase("ADMIN")) {
                                    isAdmin = true;
                                    roleName = name;
                                    break;
                                }
                            }
                            
                            // ADMIN yoksa, diğer sistem dışı rolü al
                            if (!isAdmin) {
                                for (RoleRepresentation role : userRoles) {
                                    String name = role.getName();
                                    // Sistem rollerini atla
                                    if (!name.equals("offline_access") 
                                            && !name.equals("uma_authorization")
                                            && !name.startsWith("default-roles")) {
                                        roleName = name;
                                        break;
                                    }
                                }
                            }
                            
                            if (isAdmin) {
                                response.setRole("ADMIN");
                            } else if (roleName != null) {
                                // USER kontrolü (case-insensitive)
                                if (roleName.toUpperCase().contains("USER")) {
                                    response.setRole("USER");
                                } else {
                                    // Bilinmeyen rol için default USER
                                    response.setRole("USER");
                                }
                            } else {
                                // Sadece sistem rolleri varsa default USER
                                response.setRole("USER");
                            }
                        } else {
                            // Role yoksa default USER
                            response.setRole("USER");
                        }
                    } catch (Exception e) {
                        // Hata durumunda default USER
                        response.setRole("USER");
                        System.err.println("Error getting user role for user " + user.getId() + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                    
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserRepresentation getUserInfoByToken(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(adminClientSecret.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setId(claims.getSubject());
            userRepresentation.setUsername(claims.get("preferred_username", String.class));
            userRepresentation.setEmail(claims.get("email", String.class));
            userRepresentation.setFirstName(claims.get("given_name", String.class));
            userRepresentation.setLastName(claims.get("family_name", String.class));

            return userRepresentation;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while parsing the token", e);
        }
    }
}