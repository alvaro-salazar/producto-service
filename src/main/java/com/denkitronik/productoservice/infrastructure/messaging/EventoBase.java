package com.denkitronik.productoservice.infrastructure.messaging;

import java.time.Instant;
import java.util.UUID;

public record EventoBase<T>(
    String eventoId,
    String eventoTipo,
    String version,
    Instant ocurrioEn,
    String servicioOrigen,
    T payload
) {
    public static <T> EventoBase<T> of(String tipo, T payload) {
        return new EventoBase<>(
            UUID.randomUUID().toString(), tipo, "1.0",
            Instant.now(), "producto-service", payload);
    }
}
