package co.edu.uceva.productoservice.domain.repository;

import co.edu.uceva.productoservice.domain.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface que hereda de CrudRepository para realizar las operaciones de CRUD sobre la entidad Producto
 * CRUD: Create, Retrieve, Update, Delete
 */
public interface IProductoRepository extends JpaRepository<Producto, Long> {
}
