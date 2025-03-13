package co.edu.uceva.productoservice.domain.service;

import co.edu.uceva.productoservice.domain.exception.ProductoExistenteException;
import co.edu.uceva.productoservice.domain.exception.ProductoNoEncontradoException;
import co.edu.uceva.productoservice.domain.model.Producto;
import co.edu.uceva.productoservice.domain.repository.IProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements IProductoService {

    IProductoRepository productoRepository;

    @Autowired
    public ProductoServiceImpl(IProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public Producto save(Producto producto) {
        if (productoRepository.findByNombre((producto.getNombre())).isPresent()) {
            throw new ProductoExistenteException(producto.getNombre());
        }
        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void delete(Producto producto) {
        productoRepository.delete(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    @Transactional
    public Producto update(Producto producto) {
        if (productoRepository.findById(producto.getId()).isEmpty()) {
            throw new ProductoNoEncontradoException(producto.getId());
        }
        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public Page<Producto> findAll(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

}
