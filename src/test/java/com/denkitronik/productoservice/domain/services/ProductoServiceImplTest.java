package com.denkitronik.productoservice.domain.services;

import com.denkitronik.productoservice.domain.entities.Producto;
import com.denkitronik.productoservice.domain.exception.ProductoNotFoundException;
import com.denkitronik.productoservice.domain.repositories.IProductoDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private IProductoDao productoDao;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Hamburguesa Test");
        producto.setDescripcion("Descripcion de prueba");
        producto.setPrecio(new BigDecimal("12500.00"));
        producto.setCategoria("hamburguesas");
    }

    @Test
    void findAll_devuelveListaDeProductos() {
        when(productoDao.findAll()).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Hamburguesa Test");
        verify(productoDao).findAll();
    }

    @Test
    void findById_productoExiste_devuelveProducto() {
        when(productoDao.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Hamburguesa Test");
    }

    @Test
    void findById_productoNoExiste_lanzaProductoNotFoundException() {
        when(productoDao.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.findById(99L))
            .isInstanceOf(ProductoNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void save_productoValido_retornaProductoGuardado() {
        when(productoDao.save(producto)).thenReturn(producto);

        Producto resultado = productoService.save(producto);

        assertThat(resultado.getNombre()).isEqualTo("Hamburguesa Test");
        verify(productoDao).save(producto);
    }

    @Test
    void update_productoExiste_actualizaCampos() {
        Producto actualizado = new Producto();
        actualizado.setNombre("Hamburguesa Premium");
        actualizado.setDescripcion("Nueva descripcion");
        actualizado.setPrecio(new BigDecimal("18000.00"));
        actualizado.setCategoria("hamburguesas");

        when(productoDao.findById(1L)).thenReturn(Optional.of(producto));
        when(productoDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Producto resultado = productoService.update(1L, actualizado);

        assertThat(resultado.getNombre()).isEqualTo("Hamburguesa Premium");
        assertThat(resultado.getPrecio()).isEqualByComparingTo("18000.00");
    }

    @Test
    void delete_productoExiste_eliminaCorrectamente() {
        when(productoDao.existsById(1L)).thenReturn(true);
        doNothing().when(productoDao).deleteById(1L);

        assertThatCode(() -> productoService.delete(1L)).doesNotThrowAnyException();
        verify(productoDao).deleteById(1L);
    }

    @Test
    void delete_productoNoExiste_lanzaProductoNotFoundException() {
        when(productoDao.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productoService.delete(99L))
            .isInstanceOf(ProductoNotFoundException.class);
    }

    @Test
    void actualizarImagenUrl_productoExiste_actualizaUrl() {
        when(productoDao.findById(1L)).thenReturn(Optional.of(producto));
        when(productoDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Producto resultado = productoService.actualizarImagenUrl(1L, "http://minio/productos/img.jpg");

        assertThat(resultado.getImagenUrl()).isEqualTo("http://minio/productos/img.jpg");
    }
}
