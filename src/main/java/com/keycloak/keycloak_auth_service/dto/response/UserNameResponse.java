package com.keycloak.keycloak_auth_service.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNameResponse {
    private String id;
    private String username;
}
