package com.denkitronik.productoservice.domain.services;

import com.denkitronik.productoservice.domain.entities.Producto;
import com.denkitronik.productoservice.domain.exception.ProductoNotFoundException;
import com.denkitronik.productoservice.domain.exception.ProductoServiceException;
import com.denkitronik.productoservice.domain.repositories.IProductoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductoServiceImpl implements IProductoService {

    @Autowired
    private IProductoDao productoDao;

    @Override
    @Transactional(readOnly = true)
    public Page<Producto> findAll(Pageable pageable) {
        return productoDao.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findById(Long id) {
        return productoDao.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
    }

    @Override
    @Transactional
    public Producto save(Producto producto) {
        try {
            return productoDao.save(producto);
        } catch (DataIntegrityViolationException ex) {
            throw new ProductoServiceException(
                "Error al guardar el producto: datos duplicados o restricción violada",
                ex
            );
        }
    }

    @Override
    @Transactional
    public Producto update(Long id, Producto producto) {
        Producto actual = productoDao.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        actual.setNombre(producto.getNombre());
        actual.setDescripcion(producto.getDescripcion());
        actual.setPrecio(producto.getPrecio());
        actual.setCategoria(producto.getCategoria());
        return productoDao.save(actual);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!productoDao.existsById(id)) {
            throw new ProductoNotFoundException(id);
        }
        productoDao.deleteById(id);
    }

    @Override
    @Transactional
    public Producto actualizarImagenUrl(Long id, String imagenUrl) {
        Producto actual = productoDao.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        actual.setImagenUrl(imagenUrl);
        return productoDao.save(actual);
    }
}
