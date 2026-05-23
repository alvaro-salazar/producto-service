package com.denkitronik.productoservice.infrastructure.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaSagaConfig {

    @Bean
    public NewTopic topicInventarioReservado() {
        return TopicBuilder.name("inventario.reservado").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic topicInventarioInsuficiente() {
        return TopicBuilder.name("inventario.insuficiente").partitions(3).replicas(1).build();
    }
}
