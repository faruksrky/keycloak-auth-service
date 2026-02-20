package com.keycloak.keycloak_auth_service.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {
    @Value("${app.keycloak.serverUrl}")
    private String serverUrl;
    @Value("${app.keycloak.realm}")
    private String realm;
    @Value("${app.keycloak.admin.adminClientId}")
    private String clientId;
    @Value("${app.keycloak.admin.adminClientSecret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloakAdmin() {
        // admin-cli client master realm'de - authentication için master kullan
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}

