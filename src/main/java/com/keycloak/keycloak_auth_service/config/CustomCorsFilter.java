package com.keycloak.keycloak_auth_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomCorsFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(CustomCorsFilter.class);

    private static final List<String> ALLOWED_ORIGIN_PATTERNS = Arrays.asList(
        "https://.*\\.psikohekimfrontend\\.pages\\.dev",  // Regex: any subdomain (preview deployments)
        "https://psikohekimfrontend\\.pages\\.dev",        // Production
        "https://.*\\.iyihislerapp\\.com",                 // Keycloak domains (Cloudflare Tunnel)
        "https://iyihislerapp\\.com",                      // Keycloak production
        "http://localhost:.*"                              // Local development
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String origin = request.getHeader("Origin");
        String method = request.getMethod();
        String path = request.getRequestURI();
        
        // Log only CORS-related requests (info level so we can see it in logs)
        if (origin != null || "OPTIONS".equals(method)) {
            log.info("🔍 CORS Filter - Request: {} {} | Origin: {}", method, path, origin);
        }
        
        // Check if origin matches any allowed pattern
        boolean isAllowed = false;
        if (origin != null) {
            for (String pattern : ALLOWED_ORIGIN_PATTERNS) {
                if (Pattern.matches("^" + pattern + "$", origin)) {
                    isAllowed = true;
                    log.info("✅ Origin ALLOWED by pattern: {} | Origin: {}", pattern, origin);
                    break;
                }
            }
            if (!isAllowed) {
                log.warn("❌ Origin NOT ALLOWED: {} | Allowed patterns: {}", origin, ALLOWED_ORIGIN_PATTERNS);
            }
        }

        // Handle preflight OPTIONS request FIRST
        if ("OPTIONS".equalsIgnoreCase(method)) {
            if (isAllowed && origin != null) {
                // Get requested headers from Access-Control-Request-Headers
                String requestedHeaders = request.getHeader("Access-Control-Request-Headers");
                log.info("📋 Preflight Request Headers: {}", requestedHeaders);
                
                // Set CORS headers for preflight
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
                
                // Explicitly allow common headers (wildcard doesn't work with credentials)
                String allowedHeaders = "Content-Type, Authorization, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers";
                if (requestedHeaders != null && !requestedHeaders.isEmpty()) {
                    // Include requested headers plus our defaults
                    allowedHeaders = allowedHeaders + ", " + requestedHeaders;
                }
                response.setHeader("Access-Control-Allow-Headers", allowedHeaders);
                response.setHeader("Access-Control-Max-Age", "3600");
                log.info("✅ OPTIONS preflight ALLOWED, returning 200 OK | Allowed Headers: {}", allowedHeaders);
                response.setStatus(HttpServletResponse.SC_OK);
                return; // Don't continue to filter chain
            } else {
                log.warn("❌ OPTIONS preflight REJECTED (origin not allowed): {}", origin);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return; // Don't continue to filter chain
            }
        }

        // For non-OPTIONS requests, set CORS headers if origin is allowed
        if (isAllowed && origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept, Origin");
            response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");
            response.setHeader("Access-Control-Max-Age", "3600");
            log.info("✅ CORS headers SET for origin: {}", origin);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}


