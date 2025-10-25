package com.keycloak.keycloak_auth_service.service;

import com.keycloak.keycloak_auth_service.dto.request.UserRequest;
import com.keycloak.keycloak_auth_service.dto.response.UserAdminResponse;
import com.keycloak.keycloak_auth_service.dto.response.UserNameResponse;
import com.keycloak.keycloak_auth_service.dto.response.UserResponse;
import com.keycloak.keycloak_auth_service.entity.NewUserRecord;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface UserService {
    void createUser(UserRequest userRequest);
    void deleteUser(String userId);
    void forgotPassword(String username);
    UserRepresentation getUserInfoByToken(String accessToken);
    List<UserNameResponse> getAllUsernames();
    List<UserResponse> getAllUsers();
}
