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
    @Value("${app.keycloak.admin.adminClientId}")
    private String clientId;
    @Value("${app.keycloak.admin.adminClientSecret:}")
    private String clientSecret;
    @Value("${app.keycloak.admin.username:}")
    private String adminUsername;
    @Value("${app.keycloak.admin.password:}")
    private String adminPassword;

    @Bean
    public Keycloak keycloakAdmin() {
        KeycloakBuilder builder = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .clientId(clientId);

        // Admin username/password varsa kullan (daha güvenilir - Admin Console ile aynı)
        if (adminUsername != null && !adminUsername.isBlank() && adminPassword != null && !adminPassword.isBlank()) {
            return builder
                    .username(adminUsername)
                    .password(adminPassword)
                    .clientSecret(clientSecret != null ? clientSecret : "")
                    .grantType(OAuth2Constants.PASSWORD)
                    .build();
        }
        // Yoksa client credentials
        return builder
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}

