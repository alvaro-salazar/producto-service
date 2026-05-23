package com.denkitronik.productoservice.infrastructure.messaging;

import com.denkitronik.productoservice.domain.repositories.IProductoDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventarioSagaListener {

    private final IProductoDao                  productoDao;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
        topics = "inventario.reservas",
        groupId = "producto-inventario-group",
        properties = {"spring.json.value.default.type=com.denkitronik.productoservice.infrastructure.messaging.ReservaInventarioSolicitadaEvent"}
    )
    @Transactional
    public void onReservaSolicitada(ReservaInventarioSolicitadaEvent solicitud) {
        log.info("Solicitud de reserva: {} uds del producto {} para pedido {}",
                 solicitud.cantidad(), solicitud.productoId(), solicitud.pedidoId());

        productoDao.findById(solicitud.productoId()).ifPresentOrElse(
            producto -> {
                if (producto.getStock() >= solicitud.cantidad()) {
                    producto.setStock(producto.getStock() - solicitud.cantidad());
                    productoDao.save(producto);

                    var respuesta = new InventarioReservadoEvent(
                        solicitud.pedidoId(), solicitud.productoId(),
                        solicitud.cantidad(), producto.getStock());
                    kafkaTemplate.send("inventario.reservado",
                                       solicitud.pedidoId().toString(),
                                       respuesta);
                    log.info("Inventario reservado. Stock restante: {}", producto.getStock());

                } else {
                    var respuesta = new InventarioInsuficienteEvent(
                        solicitud.pedidoId(), solicitud.productoId(),
                        solicitud.cantidad(), producto.getStock());
                    kafkaTemplate.send("inventario.insuficiente",
                                       solicitud.pedidoId().toString(),
                                       respuesta);
                    log.warn("Stock insuficiente. Solicitado: {}, Disponible: {}",
                             solicitud.cantidad(), producto.getStock());
                }
            },
            () -> log.error("Producto {} no encontrado para pedido {}",
                            solicitud.productoId(), solicitud.pedidoId())
        );
    }
}
