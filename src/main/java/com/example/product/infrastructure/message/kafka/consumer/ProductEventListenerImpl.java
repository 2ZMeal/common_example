package com.example.product.infrastructure.message.kafka.consumer;

import com.example.product.application.dto.message.ProductCreateMessage;
import com.example.product.application.dto.message.ProductDeleteMessage;
import com.example.product.application.dto.message.ProductUpdateMessage;
import com.example.product.application.dto.request.ProductCreateRequest;
import com.example.product.application.dto.request.ProductUpdateRequest;
import com.example.product.application.service.ProductService;
import com.ezmeal.common.security.principal.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListenerImpl {

    private final ProductService productService;

    // 1. 상품 생성 메시지 수신
    @KafkaListener(topics = "product-create-command-topic", groupId = "product-group")
    public void handleProductCreate(ProductCreateMessage message) {
        CustomUserPrincipal principal = getAuthenticatedUser();

        if (principal != null) {
            log.info("[Kafka] 유저({})의 요청으로 상품을 생성합니다. 상품명: {}", principal.getUserId(), message.getName());
            productService.createProduct(new ProductCreateRequest(message.getName()));
        } else {
            log.info("[Kafka] 시스템 요청으로 상품을 생성합니다. 상품명: {}", message.getName());
            productService.createProductBySystem(message.getName());
        }
    }

    // 2. 상품 수정 메시지 수신
    @KafkaListener(topics = "product-update-command-topic", groupId = "product-group")
    public void handleProductUpdate(ProductUpdateMessage message) {
        CustomUserPrincipal principal = getAuthenticatedUser();

        if (principal != null) {
            log.info("[Kafka] 유저({})의 요청으로 상품을 수정합니다. ID: {}", principal.getUserId(), message.getProductId());
            productService.updateProduct(
                    message.getProductId(),
                    new ProductUpdateRequest(message.getNewName())
            );
        } else {
            log.info("[Kafka] 시스템 요청으로 상품을 수정합니다. ID: {}", message.getProductId());
            productService.updateProductBySystem(
                    message.getProductId(),
                    message.getNewName()
            );
        }
    }

    // 3. 상품 삭제 메시지 수신
    @KafkaListener(topics = "product-delete-command-topic", groupId = "product-group")
    public void handleProductDelete(ProductDeleteMessage message) {
        CustomUserPrincipal principal = getAuthenticatedUser();

        if (principal != null) {
            log.info("[Kafka] 유저({})의 요청으로 상품을 삭제합니다. ID: {}", principal.getUserId(), message.getProductId());
            productService.deleteProduct(message.getProductId(), principal.getUserId());
        } else {
            log.info("[Kafka] 시스템 요청으로 상품을 삭제합니다. ID: {}", message.getProductId());
            productService.deleteProductBySystem(message.getProductId());
        }
    }

    // 내부 공통 유틸 (인증된 유저인지 확인)
    private CustomUserPrincipal getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 존재하고, CustomUserPrincipal일 경우 반환
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserPrincipal principal) {
            return principal;
        }

        // 헤더가 없어서 인터셉터가 채우지 못한 경우 (순수 시스템 호출)
        return null;
    }
}
