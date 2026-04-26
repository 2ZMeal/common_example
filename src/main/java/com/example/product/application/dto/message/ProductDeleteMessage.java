package com.example.product.application.dto.message;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDeleteMessage {
    private UUID productId;
}
