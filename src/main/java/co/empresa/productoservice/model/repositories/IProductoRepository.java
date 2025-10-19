package co.empresa.productoservice.model.repositories;

import co.empresa.productoservice.model.entities.Producto;
import org.springframework.data.repository.CrudRepository; //using CrudRepository interface from JPA that provides methods to do CRUD

/**
 * Interface que hereda de CrudRepository para realizar
 * las operaciones CRUD sobre la entidad Producto
 */
public interface IProductoRepository extends CrudRepository<Producto, Long>{
}
