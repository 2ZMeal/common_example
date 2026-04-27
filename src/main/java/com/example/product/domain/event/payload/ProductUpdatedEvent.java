package com.example.product.domain.event.payload;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductUpdatedEvent {

    private UUID productId;
    private String newName;

    // 객체 생성용 정적 팩토리 메서드
    public static ProductUpdatedEvent of(UUID productId, String newName) {
        return new ProductUpdatedEvent(productId, newName);
    }
}
