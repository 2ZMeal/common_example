package com.example.product.domain.event;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductDeletedEvent {

    private UUID productId;

    public static ProductDeletedEvent of(UUID productId) {
        return new ProductDeletedEvent(productId);
    }
}
