package com.denkitronik.productoservice.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "productos")
@Getter
@Setter
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotEmpty(message = "La categoría no puede estar vacía")
    @Column(nullable = false)
    private String categoria;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "create_at")
    @Temporal(TemporalType.DATE)
    private Date createAt;

    @PrePersist
    public void prePersist() {
        createAt = new Date();
    }
}
