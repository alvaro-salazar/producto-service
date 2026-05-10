package com.denkitronik.productoservice.domain.services;

import com.denkitronik.productoservice.domain.entities.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IProductoService {

    /**
     * Lista todos los productos con paginación.
     */
    Page<Producto> findAll(Pageable pageable);

    /**
     * Devuelve todos los productos sin paginación.
     */
    List<Producto> findAll();

    /**
     * Busca un producto por ID.
     * @throws com.denkitronik.productoservice.domain.exception.ProductoNotFoundException
     *         si no existe un producto con ese ID
     */
    Producto findById(Long id);

    /**
     * Persiste un nuevo producto.
     * @throws com.denkitronik.productoservice.domain.exception.ProductoServiceException
     *         si ocurre un error de base de datos
     */
    Producto save(Producto producto);

    /**
     * Actualiza los campos de un producto existente preservando createAt e imagenUrl.
     * @throws com.denkitronik.productoservice.domain.exception.ProductoNotFoundException
     *         si no existe un producto con ese ID
     */
    Producto update(Long id, Producto producto);

    /**
     * Elimina un producto por ID.
     * @throws com.denkitronik.productoservice.domain.exception.ProductoNotFoundException
     *         si no existe un producto con ese ID
     */
    void delete(Long id);

    /**
     * Actualiza la URL de imagen de un producto existente.
     * @throws com.denkitronik.productoservice.domain.exception.ProductoNotFoundException
     *         si no existe un producto con ese ID
     */
    Producto actualizarImagenUrl(Long id, String imagenUrl);
}
