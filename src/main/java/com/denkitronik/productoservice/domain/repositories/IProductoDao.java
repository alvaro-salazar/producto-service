package com.denkitronik.productoservice.domain.repositories;

import com.denkitronik.productoservice.domain.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IProductoDao extends JpaRepository<Producto, Long> {

    // JpaRepository ya incluye:
    // Page<Producto> findAll(Pageable pageable);
    // Optional<Producto> findById(Long id);
    // Producto save(Producto producto);
    // void deleteById(Long id);
    // long count();
    // boolean existsById(Long id);

    List<Producto> findByCategoria(String categoria);
}
