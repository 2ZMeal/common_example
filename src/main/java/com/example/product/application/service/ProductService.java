package com.example.product.application.service;

import com.example.product.application.dto.request.ProductCreateRequest;
import com.example.product.application.dto.request.ProductUpdateRequest;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.domain.event.ProductEventProducer;
import com.example.product.domain.event.payload.ProductCreatedEvent;
import com.example.product.domain.exception.ProductErrorCode;
import com.example.product.domain.model.Product;
import com.example.product.domain.repository.ProductRepository;
import com.ezmeal.common.enums.Role;
import com.ezmeal.common.exception.types.ForbiddenException;
import com.ezmeal.common.exception.types.NotFoundException;
import com.ezmeal.common.exception.types.UnauthorizedException;
import com.ezmeal.common.security.principal.CustomUserPrincipal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventProducer eventProducer;

    // =====================================================================
    // USER ID가 제공이 되는 API 요청 / Kakfa Message인 경우
    // -> 아래의 메서드들은 JPA Auditing이 SecurityContext의 유저 ID를 자동으로 기록
    // =====================================================================

    // 상품 생성
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        // 업체 관리자인거나 마스터인지 검증
        verifyCompanyOrAdminRole();

        // 1. 엔티티 생성 (유저 팩토리 메서드 -> createdBy/modifiedBy: Audit 자동 처리)
        Product product = Product.createByUser(request.getName());
        Product savedProduct = productRepository.save(product);

        // 2. 다른 마이크로서비스에 생성 이벤트 전파 (필요시)
        // 일반적으로 Read를 제외한 Create, Update, Delete는 이벤트로 처리
        /*
        ProductCreatedEvent event = ProductCreatedEvent.of(savedProduct.getId(), savedProduct.getName());
        eventProducer.publishCreatedEvent(event);
        */

        return ProductResponse.from(savedProduct);
    }

    // 상품 단건 조회
    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID productId) {
        // 1. 조회 및 예외 처리 (조회는 누구나 가능하므로 권한 검증 없음)
        Product product = productRepository.findActiveById(productId)
                .orElseThrow(() -> new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

        return ProductResponse.from(product);
    }

    // 상품 이름 수정
    @Transactional
    public ProductResponse updateProduct(UUID productId, ProductUpdateRequest request) {
        // 업체 관리자인거나 마스터인지 검증
        verifyCompanyOrAdminRole();

        // 1. 조회 및 예외 처리
        Product product = productRepository.findActiveById(productId)
                .orElseThrow(() -> new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // 2. 상태 변경 (유저 메서드 -> modifiedBy: Audit 자동 갱신)
        product.updateNameByUser(request.getName());

        // 3. 다른 마이크로서비스에 수정 이벤트 전파 (필요시)
        // 일반적으로 Read를 제외한 Create, Update, Delete는 이벤트로 처리
        /*
        ProductUpdatedEvent event = ProductUpdatedEvent.of(product.getId(), product.getName());
        eventProducer.publishUpdatedEvent(event);
        */

        return ProductResponse.from(product);
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(UUID productId, String userId) {
        // 업체 관리자인거나 마스터인지 검증
        verifyCompanyOrAdminRole();

        // 1. 조회 및 예외 처리
        Product product = productRepository.findActiveById(productId)
                .orElseThrow(() -> new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // 2. 삭제 처리 (JPA Auditing이 지원하지 않으므로 userId 파라미터로 명시적 전달)
        product.deleteByUser(userId);

        // 3. 다른 마이크로서비스에 삭제 이벤트 전파 (필요시)
        // 일반적으로 Read를 제외한 Create, Update, Delete는 이벤트로 처리
        /*
        ProductDeletedEvent event = ProductDeletedEvent.of(productId);
        eventProducer.publishDeletedEvent(event);
        */
    }

    // =====================================================================
    // USER ID가 제공되지 않는 API 요청 / Kafka 메시지인 경우
    // -> 아래의 메서드들은 BaseEntity의 유틸 메서드를 통해 강제로 "SYSTEM"을 기록함
    // =====================================================================

    // 상품 생성
    @Transactional
    public void createProductBySystem(String name) {
        // 1. 엔티티 생성 (시스템 팩토리 메서드 -> createdBy/modifiedBy 강제로 "SYSTEM" 셋팅)
        Product product = Product.createBySystem(name);
        Product savedProduct = productRepository.save(product);

        // 2. 다른 마이크로서비스에 생성 이벤트 전파
        ProductCreatedEvent event = ProductCreatedEvent.of(savedProduct.getId(), savedProduct.getName());
        eventProducer.publishCreatedEvent(event);
    }

    // 상품 수정
    @Transactional
    public void updateProductBySystem(UUID productId, String newName) {
        // 1. 조회 및 예외 처리
        Product product = productRepository.findActiveById(productId)
                .orElseThrow(() -> new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // 2. 시스템 권한으로 수정 (modifiedBy 강제로 "SYSTEM" 셋팅)
        product.updateNameBySystem(newName);

        // 3. 다른 마이크로서비스에 수정 이벤트 전파 (필요시)
        // 일반적으로 Read를 제외한 Create, Update, Delete는 이벤트로 처리
        /*
        ProductUpdatedEvent event = ProductUpdatedEvent.of(product.getId(), product.getName());
        eventProducer.publishUpdatedEvent(event);
         */
    }

    // 상품 삭제
    @Transactional
    public void deleteProductBySystem(UUID productId) {
        // 1. 조회 및 예외 처리
        Product product = productRepository.findActiveById(productId)
                .orElseThrow(() -> new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // 2. 시스템 권한으로 삭제 (deletedBy 강제로 "SYSTEM" 셋팅)
        product.deleteProductBySystem();

        // 3. 다른 마이크로서비스에 삭제 이벤트 전파 (필요시)
        // 일반적으로 Read를 제외한 Create, Update, Delete는 이벤트로 처리
        /*
        ProductDeletedEvent event = ProductDeletedEvent.of(productId);
        eventProducer.publishDeletedEvent(event);
        */
    }

    // 권한 검증을 위한 메서드
    private void verifyCompanyOrAdminRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 1. 인증 정보가 없거나 익명 유저인 경우 접근 거부 (401 Unauthorized)
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException(ProductErrorCode.UNAUTHORIZED_REQUEST);
        }

        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();

        // 2. 일반 유저(USER)인 경우 접근 거부 (403 Forbidden)
        if (principal.getRole() == Role.USER) {
            throw new ForbiddenException(ProductErrorCode.NOT_ENOUGH_PERMISSION);
        }
    }
}
