package co.empresa.productoservice.model.services;

import co.empresa.productoservice.model.entities.Producto;
import java.util.List;

/**
 * Interface que define los métodos que se pueden realizar sobre la entidad Producto
 */
public interface IProductoService {
    Producto save(Producto producto);
    void delete(Producto producto);
    Producto findById(long id);
    Producto update(Producto producto);
    List<Producto> findAll();
}
