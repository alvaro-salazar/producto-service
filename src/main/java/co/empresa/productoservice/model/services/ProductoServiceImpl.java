package co.empresa.productoservice.model.services;

import co.empresa.productoservice.model.entities.Producto;
import co.empresa.productoservice.model.repositories.IProductoRepository;
import org.springframework.stereotype.Service; //allows use @Service
import java.util.List;

/**
 * Clase que implementa los métodos de la interfaz IProductoService
 * para realizar las operaciones de negocio sobre la entidad Producto
 */
@Service
public class ProductoServiceImpl implements IProductoService {
    IProductoRepository productoRepository;

    //constructor de la clase
    public ProductoServiceImpl(IProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto save(Producto producto){
        return productoRepository.save(producto);
    }

    @Override
    public void delete(Producto producto){
        productoRepository.delete(producto);
    }

    @Override
    public Producto findById(Long id){
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public Producto update(Producto producto){
        return productoRepository.save(producto);
    }

    @Override
    public List<Producto> findAll(){
        return (List<Producto>) productoRepository.findAll();
    }

}
