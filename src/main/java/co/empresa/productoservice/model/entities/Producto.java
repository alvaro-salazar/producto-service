package co.empresa.productoservice.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; //it allows me define the entity ok
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //pk is id. Its type is Long and serial in postgres
    private String nombre; //columns of entity
    private String descripcion;
    private Double precio;

}
