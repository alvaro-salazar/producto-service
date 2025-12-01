package co.empresa.productoservice.delivery.rest;

import co.empresa.productoservice.domain.exception.NoHayProductosException;
import co.empresa.productoservice.domain.exception.PaginaSinProductosException;
import co.empresa.productoservice.domain.exception.ProductoNoEncontradoException;
import co.empresa.productoservice.domain.exception.ValidationException;
import co.empresa.productoservice.domain.model.Producto;
import co.empresa.productoservice.domain.service.IProductoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/producto")
public class ProductoController {

    // Declaramos como final el servicio para mejorar la inmutabilidad
    private final IProductoService productoService;

    // Constantes para los mensajes de respuesta
    private static final String MENSAJE = "mensaje";
    private static final String PRODUCTO = "producto";
    private static final String PRODUCTOS = "productos";

    // Inyección de dependencia del servicio que proporciona servicios de CRUD
    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Listar todos los productos.
     */
    @GetMapping("/productos")
    public ResponseEntity<Map<String, Object>> getProductos() {
        List<Producto> productos = productoService.findAll();
        if (productos.isEmpty()) {
            throw new NoHayProductosException();
        }
        Map<String, Object> response = new HashMap<>();
        response.put(PRODUCTOS, productos);
        return ResponseEntity.ok(response);
    }

    /**
     * Listar productos con paginación.
     */
    @GetMapping("/producto/page/{page}")
    public ResponseEntity<Object> index(@PathVariable Integer page) {
        Pageable pageable = PageRequest.of(page, 4);
        Page<Producto> productos = productoService.findAll(pageable);
        if (productos.isEmpty()) {
            throw new PaginaSinProductosException(page);
        }
        return ResponseEntity.ok(productos);
    }

    /**
     * Crear un nuevo producto pasando el objeto en el cuerpo de la petición, usando validaciones
     */
    @PostMapping("/productos")
    public ResponseEntity<Map<String, Object>> save(@Valid @RequestBody Producto producto, BindingResult result) {
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
        Map<String, Object> response = new HashMap<>();
        Producto nuevoProducto = productoService.save(producto);
        response.put(MENSAJE, "El producto ha sido creado con éxito!");
        response.put(PRODUCTO, nuevoProducto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * Eliminar un producto pasando el objeto en el cuerpo de la petición.
     */
    @DeleteMapping("/productos")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody Producto producto) {
        productoService.findById(producto.getId())
            .orElseThrow(() -> new ProductoNoEncontradoException(producto.getId()));
        productoService.delete(producto);
        Map<String, Object> response = new HashMap<>();
        response.put(MENSAJE, "El producto ha sido eliminado con éxito!");
        response.put(PRODUCTO, null);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar un producto pasando el objeto en el cuerpo de la petición.
     * @param producto: Objeto Producto que se va a actualizar
     */
    @PutMapping("/productos")
    public ResponseEntity<Map<String, Object>> update(@Valid @RequestBody Producto producto, BindingResult result) {
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
        productoService.findById(producto.getId())
                .orElseThrow(() -> new ProductoNoEncontradoException(producto.getId()));
        Map<String, Object> response = new HashMap<>();
        Producto productoActualizado = productoService.update(producto);
        response.put(MENSAJE, "El producto ha sido actualizado con éxito!");
        response.put(PRODUCTO, productoActualizado);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener un producto por su ID.
     */
    @GetMapping("/productos/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable Long id) {
        Producto producto = productoService.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
        Map<String, Object> response = new HashMap<>();
        response.put(MENSAJE, "El producto ha sido encontrado con éxito!");
        response.put(PRODUCTO, producto);
        return ResponseEntity.ok(response);
    }
}
