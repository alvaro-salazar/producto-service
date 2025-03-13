package co.edu.uceva.productoservice.domain.service;

import co.edu.uceva.productoservice.domain.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductoService {
    Producto save(Producto producto);
    void delete(Producto producto);
    Producto findById(Long id);
    Producto update(Producto producto);
    List<Producto> findAll();
    Page<Producto> findAll(Pageable pageable);
}
