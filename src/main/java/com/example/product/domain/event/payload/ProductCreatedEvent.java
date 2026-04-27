package com.example.product.domain.event.payload;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductCreatedEvent {

    private UUID productId;
    private String name;

    public static ProductCreatedEvent of(UUID productId, String name) {
        return new ProductCreatedEvent(productId, name);
    }
}
