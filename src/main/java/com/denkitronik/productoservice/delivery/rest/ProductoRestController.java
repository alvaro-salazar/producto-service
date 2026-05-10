package com.denkitronik.productoservice.delivery.rest;

import com.denkitronik.productoservice.domain.entities.Producto;
import com.denkitronik.productoservice.domain.services.IProductoService;
import com.denkitronik.productoservice.infrastructure.minio.MinioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/${api.version}/producto-service")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ProductoRestController {

    @Autowired
    private IProductoService productoService;

    @Autowired
    private MinioService minioService;

    // ── GET /productos ────────────────────────────────────────────────────────
    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    // ── GET /productos/page/{page} ────────────────────────────────────────────
    @GetMapping("/productos/page/{page}")
    public ResponseEntity<Page<Producto>> listarProductosPaginado(@PathVariable Integer page) {
        return ResponseEntity.ok(productoService.findAll(PageRequest.of(page, 8)));
    }

    // ── GET /productos/{id} ───────────────────────────────────────────────────
    @GetMapping("/productos/{id}")
    public ResponseEntity<Producto> buscarProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    // ── POST /productos ───────────────────────────────────────────────────────
    @PostMapping("/productos")
    public ResponseEntity<?> crearProducto(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.save(producto));
    }

    // ── PUT /productos/{id} ───────────────────────────────────────────────────
    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @Valid @RequestBody Producto producto,
            @PathVariable Long id) {
        return ResponseEntity.ok(productoService.update(id, producto));
    }

    // ── DELETE /productos/{id} ────────────────────────────────────────────────
    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── POST /productos/{id}/imagen ───────────────────────────────────────────
    /**
     * Sube una imagen a MinIO y actualiza imagenUrl en el producto.
     *
     * Acepta multipart/form-data con el campo "imagen".
     * Devuelve el producto actualizado con la nueva URL de imagen.
     */
    @PostMapping("/productos/{id}/imagen")
    public ResponseEntity<Producto> subirImagen(
            @PathVariable Long id,
            @RequestParam("imagen") MultipartFile imagen) {

        String imagenUrl = minioService.subirImagen(imagen);
        Producto actualizado = productoService.actualizarImagenUrl(id, imagenUrl);
        return ResponseEntity.ok(actualizado);
    }

    // ── GET /productos/info ───────────────────────────────────────────────────
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
            "servicio", "producto-service",
            "version",  "codelab-11",
            "total",    productoService.findAll().size()
        ));
    }
}
