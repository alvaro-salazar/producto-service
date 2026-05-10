package com.denkitronik.productoservice.domain.exception;

public class ProductoNotFoundException extends RuntimeException {

    private final Long id;

    public ProductoNotFoundException(Long id) {
        super("Producto con id " + id + " no encontrado");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
