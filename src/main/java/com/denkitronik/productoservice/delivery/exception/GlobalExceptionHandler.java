package com.denkitronik.productoservice.delivery.exception;

import com.denkitronik.productoservice.domain.exception.ProductoNotFoundException;
import com.denkitronik.productoservice.domain.exception.ProductoServiceException;
import com.denkitronik.productoservice.infrastructure.minio.MinioUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── 404 Not Found ─────────────────────────────────────────
    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> manejarNotFound(ProductoNotFoundException ex) {
        log.warn("Producto no encontrado: id={}", ex.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage())
        );
    }

    // ── 500 Internal Server Error ─────────────────────────────
    @ExceptionHandler(ProductoServiceException.class)
    public ResponseEntity<ApiErrorResponse> manejarServiceException(ProductoServiceException ex) {
        log.error("Error en el servicio de productos", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Error interno del servidor. Por favor intenta más tarde."
            )
        );
    }

    // ── 422 Unprocessable Entity: error al subir imagen ───────
    @ExceptionHandler(MinioUploadException.class)
    public ResponseEntity<ApiErrorResponse> manejarMinioException(MinioUploadException ex) {
        log.error("Error al subir imagen a MinIO", ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
            new ApiErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Unprocessable Entity",
                "No se pudo procesar la imagen. Verifica el formato y tamaño."
            )
        );
    }

    // ── 400 Bad Request (validaciones @Valid) ─────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult()
          .getAllErrors()
          .forEach(error -> {
              String campo   = ((FieldError) error).getField();
              String mensaje = error.getDefaultMessage();
              errores.put(campo, mensaje);
          });
        log.debug("Errores de validación: {}", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // ── 500 genérico ──────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> manejarExcepcionGenerica(Exception ex) {
        log.error("Excepción no manejada", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Error inesperado. Por favor contacta al administrador."
            )
        );
    }
}
