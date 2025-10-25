package com.keycloak.keycloak_auth_service.service;


import com.keycloak.keycloak_auth_service.dto.response.TokenDto;
import com.keycloak.keycloak_auth_service.dto.response.UserResponse;

import java.util.Map;

public interface KeycloakService {

    TokenDto getToken (String username, String password);
    TokenDto getTokenAdmin (String username, String password);
    Map<String, Object> parseToken(String jwt);
    UserResponse extractUserInfo(String jwt);

}