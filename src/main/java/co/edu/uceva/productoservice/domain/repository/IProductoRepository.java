package co.edu.uceva.productoservice.domain.repository;

import co.edu.uceva.productoservice.domain.model.Producto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Interface que hereda de CrudRepository para realizar las operaciones de CRUD sobre la entidad Producto
 * CRUD: Create, Retrieve, Update, Delete
 */
public interface IProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Object> findByNombre(@NotEmpty(message ="No puede estar vacio") @Size(min=2, max=20, message="El tamaño tiene que estar entre 2 y 20") String nombre);
}
