package co.edu.uceva.productoservice.delivery.exception;

import co.edu.uceva.productoservice.domain.exception.NoHayProductosException;
import co.edu.uceva.productoservice.domain.exception.PaginaSinProductosException;
import co.edu.uceva.productoservice.domain.exception.ProductoNoEncontradoException;
import co.edu.uceva.productoservice.domain.exception.ProductoExistenteException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaginaSinProductosException.class)
    public ResponseEntity<Map<String, Object>> handlePaginaSinProductos(PaginaSinProductosException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Número de página inválido.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHayProductosException.class)
    public ResponseEntity<Map<String, Object>> handleNoHayProductos(NoHayProductosException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleProductoNoEncontrado(ProductoNoEncontradoException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductoExistenteException.class)
    public ResponseEntity<Map<String, Object>> handleProductoExistente(ProductoExistenteException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Error inesperado: " + ex.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(DataAccessException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Error al acceder a la base de datos.");
        response.put("error", ex.getMessage().concat(": ").concat(ex.getMostSpecificCause().getMessage()));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
