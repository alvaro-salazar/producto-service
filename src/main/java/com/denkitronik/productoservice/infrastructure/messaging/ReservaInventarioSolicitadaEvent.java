package com.denkitronik.productoservice.infrastructure.messaging;

public record ReservaInventarioSolicitadaEvent(
    Long    pedidoId,
    Long    productoId,
    Integer cantidad,
    Long    clienteId
) {}
