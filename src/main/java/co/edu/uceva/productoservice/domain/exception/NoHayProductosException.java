package co.edu.uceva.productoservice.domain.exception;

public class NoHayProductosException extends RuntimeException {
    public NoHayProductosException() {
        super("No hay productos en la base de datos.");
    }
}