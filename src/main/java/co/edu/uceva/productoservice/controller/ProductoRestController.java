package co.edu.uceva.productoservice.controller;

import co.edu.uceva.productoservice.model.entities.Producto;
import co.edu.uceva.productoservice.model.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // Metodo que retorna todos los productos
    @GetMapping("/productos")
    public List<Producto> getProductos(){
        return productoService.findAll();
    }

    @GetMapping("/producto/page/{page}")
    public Page<Producto> index(@PathVariable Integer page) {
        Pageable pageable = PageRequest.of(page, 4);
        return productoService.findAll(pageable);
    }

    // Metodo que retorna un producto por su id
    @GetMapping("/productos/{id}")
    public Producto getProductosById(@PathVariable("id") Long id) {
        return productoService.findById(id);
    }

    // Metodo que guarda un producto
    @PostMapping("/productos")
    public Producto save(@RequestBody Producto producto) {
        return productoService.save(producto);
    }

    // Metodo que elimina un producto
    @DeleteMapping("/productos")
    public void delete(@RequestBody Producto producto) {
        productoService.delete(producto);
    }

    // Metodo que actualiza un producto
    @PutMapping("/productos")
    public Producto update(@RequestBody Producto producto) {
        return productoService.update(producto);
    }

}
