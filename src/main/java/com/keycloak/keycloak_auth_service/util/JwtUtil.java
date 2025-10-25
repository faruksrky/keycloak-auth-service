package com.keycloak.keycloak_auth_service.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

public class JwtUtil {

    public static Map<String, Object> decodeJwt(String jwt) {
        try {
            String[] parts = jwt.split("\\."); // header.payload.signature
            String payload = parts[1];

            byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
            String json = new String(decodedBytes);

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, Map.class);

        } catch (Exception e) {
            throw new RuntimeException("Token çözümlenemedi: " + e.getMessage(), e);
        }
    }
}
