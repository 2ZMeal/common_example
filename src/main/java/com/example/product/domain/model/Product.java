package com.example.product.domain.model;

import com.ezmeal.common.entity.BaseEntity;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "p_product")
@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private UUID id;

    @Column(length = 100, nullable = false)
    private String name;

    private Product(String name) {
        this.name = name;
    }

    // 사람의 요청에 의해 상품을 생성하는 경우 (정적 팩토리 메서드)
    public static Product createByUser(String name) {
        return new Product(name);
    }

    // 시스템에 의해 상품을 생성하는 경우 (정적 팩토리 메서드)
    public static Product createBySystem(String name) {
        Product product = new Product(name);
        product.setSystemCreated();
        return product;
    }

    // 사람의 요청에 의해 상품을 수정하는 경우
    public void updateNameByUser(String newName) {
        this.name = newName;
    }

    // 시스템에 의해 상품을 수정하는 경우
    public void updateNameBySystem(String newName) {
        this.name = newName;
        this.setSystemModified();
    }

    // 사람의 요청에 의해 상품을 삭제하는 경우
    public void deleteByUser(String userId) {
        this.delete(userId);
    }

    // 시스템에 의해 상품을 삭제하는 경우
    public void deleteProductBySystem() {
        this.deleteBySystem();
    }
}
