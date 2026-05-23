package com.denkitronik.productoservice.infrastructure.messaging;

public record InventarioReservadoEvent(
    Long    pedidoId,
    Long    productoId,
    Integer cantidadReservada,
    Integer stockRestante
) {}
