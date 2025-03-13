package co.edu.uceva.productoservice.delivery.rest;

import co.edu.uceva.productoservice.domain.model.Producto;
import co.edu.uceva.productoservice.domain.service.IProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
import java.util.stream.Collectors;

/**
 * Clase controladora que expone los servicios REST de la entidad Producto
 * @RestController: Indica que esta clase es un controlador REST
 * @RequestMapping: Indica la URL base para acceder a los servicios de esta clase
 * @Autowired: Inyección de dependencias: Spring se encarga de instanciar la clase que se pasa como parámetro
 * @GetMapping: Indica que este metodo responde a una peticion GET
 * @PostMapping: Indica que este metodo responde a una peticion POST
 * @DeleteMapping: Indica que este metodo responde a una petición DELETE
 * @PutMapping: Indica que este metodo responde a una peticion PUT
 * @PathVariable: Indica que el parámetro de entrada de un metodo es un parámetro de la URL
 * @RequestBody: Indica que el parámetro de entrada de un metodo es un objeto JSON
 */
@RestController
@RequestMapping("/api/v1/producto-service")
public class ProductoRestController {

    // Necesitamos un objeto de tipo IProductoService para poder acceder a los métodos de la interfaz
    private IProductoService productoService;

    // Inyectamos productoService en el constructor
    @Autowired
    public ProductoRestController(IProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Listar todos los productos.
     */
    @GetMapping("/productos")
    public ResponseEntity<?> getProductos() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Producto> productos = productoService.findAll();

            if (productos.isEmpty()) {
                response.put("mensaje", "No hay productos en la base de datos.");
                return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
            }

            return ResponseEntity.ok(productos);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Listar productos con paginación.
     */
    @GetMapping("/producto/page/{page}")
    public ResponseEntity<?> index(@PathVariable Integer page) {
        Map<String, Object> response = new HashMap<>();
        Pageable pageable = PageRequest.of(page, 4);

        try {
            Page<Producto> productos = productoService.findAll(pageable);

            if (productos.isEmpty()) {
                response.put("mensaje", "No hay productos en la página solicitada.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(productos);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            response.put("mensaje", "Número de página inválido.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * Obtener un producto por su ID.
     */
    @GetMapping("/productos/{id}")
    public ResponseEntity<?> getProductosById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Producto producto = productoService.findById(id);

            if (producto == null) {
                response.put("mensaje", "El producto ID: " + id + " no existe en la base de datos.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(producto);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al consultar la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Crear un nuevo producto con validaciones.
     */
    @PostMapping("/productos")
    public ResponseEntity<?> save(@Valid @RequestBody Producto producto, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            Producto nuevoProducto = productoService.save(producto);
            response.put("mensaje", "El producto ha sido creado con éxito!");
            response.put("producto", nuevoProducto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al insertar el producto en la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Eliminar un producto pasando el objeto en el cuerpo de la petición.
     */
    @DeleteMapping("/productos")
    public ResponseEntity<?> delete(@RequestBody Producto producto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Producto productoExistente = productoService.findById(producto.getId());
            if (productoExistente == null) {
                response.put("mensaje", "El producto ID: " + producto.getId() + " no existe en la base de datos.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            productoService.delete(producto);
            response.put("mensaje", "El producto ha sido eliminado con éxito!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar el producto de la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Actualizar un producto con validaciones.
     */
    @PutMapping("/productos")
    public ResponseEntity<?> update(@Valid @RequestBody Producto producto, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            if (productoService.findById(producto.getId()) == null) {
                response.put("mensaje", "Error: No se pudo editar, el producto ID: " + producto.getId() + " no existe en la base de datos.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Producto productoActualizado = productoService.save(producto);
            response.put("mensaje", "El producto ha sido actualizado con éxito!");
            response.put("producto", productoActualizado);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el producto en la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
