package co.edu.uceva.productoservice.domain.exception;

public class ProductoExistenteException extends RuntimeException {
    public ProductoExistenteException(String nombre) {
        super("El producto con nombre '" + nombre + "' ya existe.");
    }
}
