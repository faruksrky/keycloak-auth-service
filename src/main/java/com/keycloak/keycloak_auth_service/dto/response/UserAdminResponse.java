package com.keycloak.keycloak_auth_service.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAdminResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
