package com.denkitronik.productoservice.infrastructure.messaging;

public record InventarioInsuficienteEvent(
    Long    pedidoId,
    Long    productoId,
    Integer cantidadSolicitada,
    Integer stockDisponible
) {}
