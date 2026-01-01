package com.keycloak.keycloak_auth_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebConfig implements WebMvcConfigurer {

    private final JwtAuthConverter jwtAuthConverter;
    private final CustomCorsFilter customCorsFilter;

    public WebConfig(JwtAuthConverter jwtAuthConverter, CustomCorsFilter customCorsFilter) {
        this.jwtAuthConverter = jwtAuthConverter;
        this.customCorsFilter = customCorsFilter;
    }
    
    // Filter'ı en üst öncelikle kaydet (SecurityFilterChain'den ÖNCE çalışsın)
    @Bean
    public FilterRegistrationBean<CustomCorsFilter> corsFilterRegistration() {
        FilterRegistrationBean<CustomCorsFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(customCorsFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);  // En yüksek öncelik
        return registration;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)  // Custom CORS filter kullanıyoruz, Spring Security CORS'u disable et
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 🔓 Login/token endpoint'leri herkese açık olmalı
                        .requestMatchers(HttpMethod.POST, "/keycloak/getToken").permitAll()
                        .requestMatchers(HttpMethod.POST, "/keycloak/getTokenAdmin").permitAll()
                        // userInfo endpoint'i token gerektirir ama CORS preflight için açık olmalı
                        .requestMatchers(HttpMethod.OPTIONS, "/keycloak/userInfo").permitAll()
                        .requestMatchers("/error").permitAll()

                        // 🔒 Kullanıcı listesi: giriş + yetki gerekli
                        .requestMatchers(HttpMethod.GET, "/users/list")
                        .hasAnyAuthority("SCOPE_user.read", "ROLE_ADMIN")

                        // diğer tüm istekler: kimlik doğrulaması zorunlu
                        .anyRequest().authenticated()
                )
                // Resource Server: gelen JWT'yi doğrula + custom converter
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Use allowedOriginPatterns for wildcard support
        // Cloudflare Pages URL pattern'leri (hem production hem preview deployments için)
        // Spring Boot wildcard format: * matches any subdomain
        // Pattern matching: https://*.domain.com matches https://anything.domain.com
        // But Spring Boot pattern matching might be case-sensitive and specific
        // Spring Boot wildcard pattern matching
        // Pattern: https://*.domain.com matches https://anything.domain.com
        // But we need to handle Cloudflare Pages preview URLs which have format: https://[hash].psikohekimfrontend.pages.dev
        List<String> allowedPatterns = Arrays.asList(
            "https://*.psikohekimfrontend.pages.dev",  // Tüm Cloudflare Pages preview deployments (örn: 1b836a21.psikohekimfrontend.pages.dev)
            "https://psikohekimfrontend.pages.dev",    // Production domain
            "https://*.iyihislerapp.com",              // Keycloak domain pattern
            "https://iyihislerapp.com",                // Keycloak domain
            "http://localhost:*"                       // Local development
        );
        
        configuration.setAllowedOriginPatterns(allowedPatterns);
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        // NOTE: allowCredentials(true) ile wildcard (*) kullanılamaz
        // CustomCorsFilter kullanıldığı için burada false bırakıyoruz
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);  // Preflight cache 1 saat

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // WebMvcConfigurer ile de CORS ekle (Spring MVC tarafında)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                    "https://*.psikohekimfrontend.pages.dev",
                    "https://psikohekimfrontend.pages.dev",
                    "https://*.iyihislerapp.com",
                    "https://iyihislerapp.com",
                    "http://localhost:*"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders(
                    "Authorization",
                    "Content-Type",
                    "Access-Control-Allow-Origin",
                    "Access-Control-Allow-Credentials"
                )
                .allowCredentials(true)
                .maxAge(3600);
    }
}
