package com.example.product.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUpdateRequest {

    @NotBlank(message = "변경할 상품 이름을 입력해주세요.")
    private String name;

    public ProductUpdateRequest(String name) {
        this.name = name;
    }
}
