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

    // Cloudflare Pages: psikohekimfrontend.pages.dev (production) + 5b025cfd.psikohekimfrontend.pages.dev (preview)
    // ([a-zA-Z0-9-]+\\.)? = optional subdomain (hash) - matches both with and without
    private static final List<String> ALLOWED_ORIGIN_PATTERNS = Arrays.asList(
        "https://([a-zA-Z0-9-]+\\.)?psikohekimfrontend\\.pages\\.dev",  // Production + Preview
        "https://www\\.iyihislerapp\\.com",
        "https://iyihislerapp\\.com",
        "https://.*\\.iyihislerapp\\.com",
        "http://localhost:[0-9]+",
        "http://127\\.0\\.0\\.1:[0-9]+"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String origin = request.getHeader("Origin");
        String method = request.getMethod();
        String path = request.getRequestURI();
        String requestedMethod = request.getHeader("Access-Control-Request-Method");
        String requestedHeaders = request.getHeader("Access-Control-Request-Headers");
        
        // Log all CORS-related requests with details
        if (origin != null || "OPTIONS".equals(method)) {
            log.info("═══════════════════════════════════════════════════════");
            log.info("🔍 CORS Filter - Request Details:");
            log.info("   Method: {}", method);
            log.info("   Path: {}", path);
            log.info("   Origin: {}", origin);
            log.info("   Access-Control-Request-Method: {}", requestedMethod);
            log.info("   Access-Control-Request-Headers: {}", requestedHeaders);
            log.info("═══════════════════════════════════════════════════════");
        }
        
        // Check if origin matches any allowed pattern
        boolean isAllowed = false;
        String matchedPattern = null;
        if (origin != null) {
            for (String pattern : ALLOWED_ORIGIN_PATTERNS) {
                if (Pattern.matches("^" + pattern + "$", origin)) {
                    isAllowed = true;
                    matchedPattern = pattern;
                    log.info("✅ Origin MATCHED - Pattern: {} | Origin: {}", pattern, origin);
                    break;
                }
            }
            if (!isAllowed) {
                log.warn("❌ Origin REJECTED - Origin: {} | Allowed patterns: {}", origin, ALLOWED_ORIGIN_PATTERNS);
            }
        } else {
            log.debug("ℹ️  No Origin header - Same-origin request or non-CORS request");
        }

        // Handle preflight OPTIONS request FIRST
        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.info("🔄 Processing OPTIONS Preflight Request...");
            if (isAllowed && origin != null) {
                // Explicitly allow common headers (wildcard doesn't work with credentials)
                String allowedHeaders = "Content-Type, Authorization, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers";
                if (requestedHeaders != null && !requestedHeaders.isEmpty()) {
                    // Include requested headers plus our defaults
                    allowedHeaders = allowedHeaders + ", " + requestedHeaders;
                }
                
                // Set CORS headers for preflight
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
                response.setHeader("Access-Control-Allow-Headers", allowedHeaders);
                response.setHeader("Access-Control-Max-Age", "3600");
                
                log.info("✅ PREFLIGHT ALLOWED - Response Headers Set:");
                log.info("   Access-Control-Allow-Origin: {}", origin);
                log.info("   Access-Control-Allow-Credentials: true");
                log.info("   Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH");
                log.info("   Access-Control-Allow-Headers: {}", allowedHeaders);
                log.info("   Status: 200 OK");
                log.info("═══════════════════════════════════════════════════════");
                
                response.setStatus(HttpServletResponse.SC_OK);
                return; // Don't continue to filter chain
            } else {
                log.error("❌ PREFLIGHT REJECTED:");
                log.error("   Origin allowed: {}", isAllowed);
                log.error("   Origin: {}", origin);
                log.error("   Matched pattern: {}", matchedPattern);
                log.error("   Status: 403 FORBIDDEN");
                log.info("═══════════════════════════════════════════════════════");
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
            
            log.info("✅ CORS Headers Set for Actual Request:");
            log.info("   Access-Control-Allow-Origin: {}", origin);
            log.info("   Access-Control-Allow-Credentials: true");
            log.info("   Method: {} | Path: {}", method, path);
            log.info("═══════════════════════════════════════════════════════");
        } else if (origin != null && !isAllowed) {
            log.warn("⚠️  CORS Headers NOT Set - Origin not allowed for {} {}", method, path);
            log.info("═══════════════════════════════════════════════════════");
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}


