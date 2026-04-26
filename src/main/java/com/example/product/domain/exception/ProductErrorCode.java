package com.example.product.domain.exception;

import com.ezmeal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_404_1", "해당 상품을 찾을 수 없습니다."),
    DUPLICATE_PRODUCT_NAME(HttpStatus.CONFLICT, "PRODUCT_409_1", "이미 존재하는 상품 이름입니다."),
    INVALID_PRODUCT_DATA(HttpStatus.BAD_REQUEST, "PRODUCT_400_1", "상품 입력 데이터가 올바르지 않습니다."),
    UNAUTHORIZED_REQUEST(HttpStatus.UNAUTHORIZED, "PRODUCT_401_1", "인증 정보가 필요합니다."),
    NOT_ENOUGH_PERMISSION(HttpStatus.FORBIDDEN, "PRODUCT_403_1", "해당 작업을 수행할 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
