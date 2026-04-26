package com.example.product.presentation.controller;

import com.example.product.application.dto.request.ProductCreateRequest;
import com.example.product.application.dto.request.ProductUpdateRequest;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.service.ProductService;
import com.ezmeal.common.response.CommonApiResponse;
import com.ezmeal.common.security.principal.CustomUserPrincipal;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /*
    * 일반적으로 권한 검증은 service나 domain에서 일차적(필수)으로 구현하고,
    * controller에서 preauthorize를 활요한 2차 검증은 "선택"하여 적용
    * */

    // 상품 생성
    @PostMapping
    public ResponseEntity<CommonApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonApiResponse.success(response));
    }

    // 상품 단건 조회
    @GetMapping("/{productId}")
    public ResponseEntity<CommonApiResponse<ProductResponse>> getProduct(@PathVariable UUID productId) {
        ProductResponse response = productService.getProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(CommonApiResponse.success(response));
    }

    // 상품 수정
    @PutMapping("/{productId}")
    public ResponseEntity<CommonApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse response = productService.updateProduct(productId, request);
        return ResponseEntity.status(HttpStatus.OK).body(CommonApiResponse.success(response));
    }

    // 상품 삭제 (Soft Delete)
    @DeleteMapping("/{productId}")
    public ResponseEntity<CommonApiResponse<Void>> deleteProduct(
            @PathVariable UUID productId,
            // 공통 모듈의 필터에서 넣어준 사용자 정보를 사용
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        productService.deleteProduct(productId, principal.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(CommonApiResponse.success());
    }
}
