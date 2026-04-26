package com.example.product.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCreateRequest {

    @NotBlank(message = "상품 이름은 필수입니다.")
    private String name;

    public ProductCreateRequest(String name) {
        this.name = name;
    }
}
