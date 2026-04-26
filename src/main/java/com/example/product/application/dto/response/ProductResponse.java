package com.example.product.application.dto.response;

import com.example.product.domain.model.Product;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponse {

    private UUID id;
    private String name;

    // Entity를 Response로 변환하는 정적 팩토리 메서드
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName()
        );
    }
}
