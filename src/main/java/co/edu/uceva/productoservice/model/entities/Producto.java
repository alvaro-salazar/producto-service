package co.edu.uceva.productoservice.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message ="No puede estar vacio")
    @Size(min=2, max=20, message="El tamaño tiene que estar entre 2 y 20")
    @Column(nullable=false)
    private String nombre;

    @Size(max = 255, message = "La descripción no puede tener más de 255 caracteres")
    private String descripcion;

    @Min(value = 0, message = "El precio no puede ser negativo")
    @NotNull(message = "El precio es obligatorio")
    @Column(nullable = false)
    private Double precio;
}
