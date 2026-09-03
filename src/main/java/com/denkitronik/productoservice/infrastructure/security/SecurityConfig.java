package com.denkitronik.productoservice.infrastructure.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configura la seguridad del microservicio como OAuth2 Resource Server.
 *
 * Keycloak envía los roles ANIDADOS dentro del JWT:
 *
 *     "realm_access": { "roles": ["ADMIN"] }
 *
 * Spring Security por defecto lee "scope" o "scp", por eso configuramos
 * un JwtAuthenticationConverter personalizado que baja hasta ese objeto.
 *
 * Reglas de acceso:
 *  - OPTIONS                           →  libre (preflight CORS del navegador)
 *  - GET  /api/v1/producto-service/**  →  ROLE_USER o ROLE_ADMIN
 *  - POST, PUT, DELETE                 →  sólo ROLE_ADMIN
 *  - /actuator/**                      →  sin autenticación (monitoreo interno)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // Sin esto, la configuración CORS de abajo nunca se aplica.
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // El navegador manda el preflight SIN la cabecera Authorization.
                // Si esta línea falta, toda petición desde un frontend muere en 401
                // antes de llegar al controlador.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
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
     * Extrae los roles de Keycloak desde el claim anidado "realm_access".
     *
     * Ojo con el atajo que parece funcionar y no funciona:
     *
     *     rolesConverter.setAuthoritiesClaimName("realm_access.roles");
     *
     * JwtGrantedAuthoritiesConverter hace una búsqueda PLANA: busca un claim de
     * primer nivel llamado literalmente "realm_access.roles". El punto no se
     * interpreta como ruta. Como Keycloak nunca emite ese claim, el resultado es
     * cero authorities y 403 en todos los endpoints protegidos — incluso para
     * ADMIN. Por eso aquí bajamos al objeto anidado a mano.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null) {
                return List.<GrantedAuthority>of();
            }
            Object roles = realmAccess.get("roles");
            if (!(roles instanceof Collection<?> lista)) {
                return List.<GrantedAuthority>of();
            }
            return lista.stream()
                    .map(String::valueOf)
                    .map(rol -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + rol))
                    .toList();
        });
        return converter;
    }

    /**
     * Permite que el frontend de Angular (ng serve en :4200) consuma la API.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
