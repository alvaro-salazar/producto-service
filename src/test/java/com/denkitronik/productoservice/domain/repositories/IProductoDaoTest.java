package com.denkitronik.productoservice.domain.repositories;

import com.denkitronik.productoservice.domain.entities.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * @DataJpaTest: levanta únicamente el contexto JPA + H2 en memoria.
 * NO levanta el servidor web ni la seguridad OAuth2.
 * Carga import.sql automáticamente desde src/test/resources/.
 */
@DataJpaTest
class IProductoDaoTest {

    @Autowired
    private IProductoDao productoDao;

    @Test
    void findAll_retornaProductosCargadosDesdeImportSql() {
        List<Producto> productos = productoDao.findAll();
        assertThat(productos).isNotEmpty();
    }

    @Test
    void findById_productoExistente_retornaProducto() {
        List<Producto> todos = productoDao.findAll();
        Long id = todos.get(0).getId();

        Optional<Producto> resultado = productoDao.findById(id);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Producto Test A");
    }

    @Test
    void save_productoNuevo_asignaId() {
        Producto nuevo = new Producto();
        nuevo.setNombre("Combo Especial");
        nuevo.setDescripcion("Hamburguesa + papas + bebida");
        nuevo.setPrecio(new BigDecimal("25000.00"));
        nuevo.setCategoria("combos");

        Producto guardado = productoDao.save(nuevo);

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Combo Especial");
    }

    @Test
    void findByCategoria_retornaProductosDeLaCategoria() {
        List<Producto> resultado = productoDao.findByCategoria("test");
        assertThat(resultado).isNotEmpty();
        resultado.forEach(p -> assertThat(p.getCategoria()).isEqualTo("test"));
    }

    @Test
    void deleteById_eliminaProducto() {
        Producto nuevo = new Producto();
        nuevo.setNombre("Para eliminar");
        nuevo.setPrecio(new BigDecimal("1000.00"));
        nuevo.setCategoria("test");
        Producto guardado = productoDao.save(nuevo);
        Long id = guardado.getId();

        productoDao.deleteById(id);

        assertThat(productoDao.findById(id)).isEmpty();
    }
}
