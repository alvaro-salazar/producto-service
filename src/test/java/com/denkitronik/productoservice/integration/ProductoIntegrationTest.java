package com.denkitronik.productoservice.integration;

import com.denkitronik.productoservice.domain.entities.Producto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.*;

/**
 * Test E2E que levanta contenedores Docker reales:
 * - PostgreSQL (via @ServiceConnection, auto-configura datasource)
 * - MinIO (via GenericContainer, configura endpoint manualmente)
 *
 * La seguridad OAuth2 se desactiva para estos tests para simplificar
 * el flujo E2E. En un entorno real se usaría un Keycloak en contenedor.
 *
 * NOTA: Este test requiere Docker Desktop corriendo en tu máquina.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        // Desactivar validación JWT para tests E2E (sin Keycloak)
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri="
    }
)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static GenericContainer<?> minio =
        new GenericContainer<>("minio/minio:latest")
            .withExposedPorts(9000)
            .withEnv("MINIO_ROOT_USER", "minioadmin")
            .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
            .withCommand("server", "/data");

    @DynamicPropertySource
    static void configurarMinio(DynamicPropertyRegistry registry) {
        registry.add("minio.endpoint",
            () -> "http://" + minio.getHost() + ":" + minio.getMappedPort(9000));
        registry.add("minio.access-key", () -> "minioadmin");
        registry.add("minio.secret-key", () -> "minioadmin");
        registry.add("minio.bucket",     () -> "productos-test");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    private static final AtomicLong idCreado = new AtomicLong();

    @Test
    @Order(1)
    void crearProducto_retorna201() {
        Producto nuevo = new Producto();
        nuevo.setNombre("Pizza Test");
        nuevo.setDescripcion("Pizza de prueba");
        nuevo.setPrecio(new BigDecimal("20000.00"));
        nuevo.setCategoria("pizzas");

        ResponseEntity<Producto> respuesta = restTemplate.postForEntity(
            "/api/v1/producto-service/productos",
            nuevo,
            Producto.class
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getId()).isNotNull();
        idCreado.set(respuesta.getBody().getId());
    }

    @Test
    @Order(2)
    void listarProductos_retorna200ConAlMenosUno() {
        ResponseEntity<Producto[]> respuesta = restTemplate.getForEntity(
            "/api/v1/producto-service/productos",
            Producto[].class
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isNotEmpty();
    }

    @Test
    @Order(3)
    void buscarProductoPorId_retorna200() {
        Long id = idCreado.get();
        ResponseEntity<Producto> respuesta = restTemplate.getForEntity(
            "/api/v1/producto-service/productos/" + id,
            Producto.class
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody().getNombre()).isEqualTo("Pizza Test");
    }

    @Test
    @Order(4)
    void actualizarProducto_retorna200() {
        Long id = idCreado.get();
        Producto actualizado = new Producto();
        actualizado.setNombre("Pizza Test Actualizada");
        actualizado.setDescripcion("Descripcion actualizada");
        actualizado.setPrecio(new BigDecimal("22000.00"));
        actualizado.setCategoria("pizzas");

        restTemplate.put("/api/v1/producto-service/productos/" + id, actualizado);

        ResponseEntity<Producto> verificacion = restTemplate.getForEntity(
            "/api/v1/producto-service/productos/" + id,
            Producto.class
        );
        assertThat(verificacion.getBody().getNombre()).isEqualTo("Pizza Test Actualizada");
    }

    @Test
    @Order(5)
    void eliminarProducto_retorna204() {
        Long id = idCreado.get();
        restTemplate.delete("/api/v1/producto-service/productos/" + id);

        ResponseEntity<String> verificacion = restTemplate.getForEntity(
            "/api/v1/producto-service/productos/" + id,
            String.class
        );
        assertThat(verificacion.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
