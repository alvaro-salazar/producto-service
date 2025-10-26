package co.empresa.productoservice.controllers;

import co.empresa.productoservice.model.entities.Producto;
import co.empresa.productoservice.model.services.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/producto-service")
public class ProductoRestController {
    private IProductoService productoService;

    @Autowired
    public ProductoRestController(IProductoService productoService){
        this.productoService = productoService;
    }

    @GetMapping("/productos")
    public List<Producto> getProductos(){
        return productoService.findAll();
    }

    @PostMapping("/productos")
    public Producto save(@RequestBody Producto producto){
        return productoService.save(producto);
    }

    @DeleteMapping("/productos")
    public void delete(@RequestBody Producto producto){
        productoService.delete(producto);
    }

    @PutMapping("/producto")
    public Producto update(@RequestBody Producto producto){
        return productoService.update(producto);
    }

    @GetMapping("/productos/{id}")
    public Producto findById(@PathVariable Long id){
        return productoService.findById(id);
    }

}
