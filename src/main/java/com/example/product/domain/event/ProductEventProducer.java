package com.example.product.domain.event;

import com.example.product.domain.event.payload.ProductCreatedEvent;
import com.example.product.domain.event.payload.ProductDeletedEvent;
import com.example.product.domain.event.payload.ProductUpdatedEvent;

public interface ProductEventProducer {
    void publishCreatedEvent(ProductCreatedEvent event);
    void publishUpdatedEvent(ProductUpdatedEvent event);
    void publishDeletedEvent(ProductDeletedEvent event);
}
