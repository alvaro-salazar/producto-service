package com.denkitronik.productoservice.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configura la seguridad del microservicio como OAuth2 Resource Server.
 *
 * Keycloak envía los roles dentro del JWT en el claim "realm_access.roles".
 * Spring Security por defecto lee "scope" o "scp", por eso configuramos
 * un JwtAuthenticationConverter personalizado que extrae los roles de Keycloak.
 *
 * Reglas de acceso:
 *  - GET  /api/v1/producto-service/**  →  cualquier usuario autenticado (ROLE_USER o ROLE_ADMIN)
 *  - POST, PUT, DELETE, PATCH         →  sólo ROLE_ADMIN
 *  - /actuator/**                     →  sin autenticación (monitoreo interno)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Actuator sin autenticación
                .requestMatchers("/actuator/**").permitAll()
                // Lectura: USER o ADMIN
                .requestMatchers(HttpMethod.GET, "/api/v1/producto-service/**")
                    .hasAnyRole("USER", "ADMIN")
                // Escritura: sólo ADMIN
                .requestMatchers(HttpMethod.POST, "/api/v1/producto-service/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/producto-service/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/producto-service/**")
                    .hasRole("ADMIN")
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    /**
     * Extrae los roles de Keycloak desde el claim "realm_access.roles" del JWT.
     * Añade el prefijo "ROLE_" que Spring Security necesita para hasRole().
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthoritiesClaimName("realm_access.roles");
        rolesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(rolesConverter);
        return converter;
    }
}
