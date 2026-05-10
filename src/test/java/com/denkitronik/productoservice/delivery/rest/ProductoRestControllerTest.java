package com.denkitronik.productoservice.delivery.rest;

import com.denkitronik.productoservice.domain.entities.Producto;
import com.denkitronik.productoservice.domain.exception.ProductoNotFoundException;
import com.denkitronik.productoservice.domain.services.IProductoService;
import com.denkitronik.productoservice.infrastructure.minio.MinioService;
import com.denkitronik.productoservice.infrastructure.security.SecurityConfig;
import org.springframework.context.annotation.Import;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @WebMvcTest: levanta sólo la capa Web (controladores, filtros, seguridad).
 * No levanta JPA ni MinIO reales — los servicios se mockean con @MockitoBean.
 *
 * Para simular tokens JWT usamos SecurityMockMvcRequestPostProcessors.jwt()
 * que inyecta un JWT de prueba sin necesidad de Keycloak.
 */
@WebMvcTest(ProductoRestController.class)
@Import(SecurityConfig.class)
class ProductoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IProductoService productoService;

    @MockitoBean
    private MinioService minioService;

    // Evita que Spring intente contactar Keycloak para obtener las claves JWKS
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Producto productoEjemplo() {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Hamburguesa Test");
        p.setDescripcion("Descripcion de prueba");
        p.setPrecio(new BigDecimal("12500.00"));
        p.setCategoria("hamburguesas");
        return p;
    }

    // ── GET /productos ────────────────────────────────────────────────────────

    @Test
    void getProductos_conRolUser_retorna200() throws Exception {
        when(productoService.findAll()).thenReturn(List.of(productoEjemplo()));

        mockMvc.perform(get("/api/v1/producto-service/productos")
                .with(jwt().authorities(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")
                )))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombre").value("Hamburguesa Test"));
    }

    @Test
    void getProductos_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(get("/api/v1/producto-service/productos"))
            .andExpect(status().isUnauthorized());
    }

    // ── GET /productos/{id} ───────────────────────────────────────────────────

    @Test
    void getProducto_idExistente_retorna200() throws Exception {
        when(productoService.findById(1L)).thenReturn(productoEjemplo());

        mockMvc.perform(get("/api/v1/producto-service/productos/1")
                .with(jwt().authorities(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")
                )))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getProducto_idInexistente_retorna404() throws Exception {
        when(productoService.findById(99L)).thenThrow(new ProductoNotFoundException(99L));

        mockMvc.perform(get("/api/v1/producto-service/productos/99")
                .with(jwt().authorities(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")
                )))
            .andExpect(status().isNotFound());
    }

    // ── POST /productos ───────────────────────────────────────────────────────

    @Test
    void crearProducto_conRolAdmin_retorna201() throws Exception {
        Producto nuevo = productoEjemplo();
        nuevo.setId(null);
        when(productoService.save(any())).thenReturn(productoEjemplo());

        mockMvc.perform(post("/api/v1/producto-service/productos")
                .with(jwt().authorities(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")
                ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crearProducto_conRolUser_retorna403() throws Exception {
        Producto nuevo = productoEjemplo();
        nuevo.setId(null);

        mockMvc.perform(post("/api/v1/producto-service/productos")
                .with(jwt().authorities(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")
                ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
            .andExpect(status().isForbidden());
    }

    // ── DELETE /productos/{id} ────────────────────────────────────────────────

    @Test
    void eliminarProducto_conRolAdmin_retorna204() throws Exception {
        doNothing().when(productoService).delete(1L);

        mockMvc.perform(delete("/api/v1/producto-service/productos/1")
                .with(jwt().authorities(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")
                )))
            .andExpect(status().isNoContent());
    }

    @Test
    void eliminarProducto_conRolUser_retorna403() throws Exception {
        mockMvc.perform(delete("/api/v1/producto-service/productos/1")
                .with(jwt().authorities(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")
                )))
            .andExpect(status().isForbidden());
    }
}
