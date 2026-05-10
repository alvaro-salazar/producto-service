package com.denkitronik.productoservice.domain.exception;

public class ProductoServiceException extends RuntimeException {

    public ProductoServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
