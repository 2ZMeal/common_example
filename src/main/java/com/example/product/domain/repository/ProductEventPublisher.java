package com.example.product.domain.repository;

import com.example.product.domain.event.ProductCreatedEvent;
import com.example.product.domain.event.ProductDeletedEvent;
import com.example.product.domain.event.ProductUpdatedEvent;

public interface ProductEventPublisher {
    void publishCreatedEvent(ProductCreatedEvent event);
    void publishUpdatedEvent(ProductUpdatedEvent event);
    void publishDeletedEvent(ProductDeletedEvent event);
}
