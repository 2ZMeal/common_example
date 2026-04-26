package com.example.product.infrastructure.message;

import com.example.product.domain.event.ProductCreatedEvent;
import com.example.product.domain.event.ProductDeletedEvent;
import com.example.product.domain.event.ProductUpdatedEvent;
import com.example.product.domain.repository.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProductEventPublisher implements ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishCreatedEvent(ProductCreatedEvent event) {
        kafkaTemplate.send("product-created-topic", event);
    }

    @Override
    public void publishUpdatedEvent(ProductUpdatedEvent event) {
        kafkaTemplate.send("product-updated-topic", event);
    }

    @Override
    public void publishDeletedEvent(ProductDeletedEvent event) {
        kafkaTemplate.send("product-deleted-topic", event);
    }
}
