package com.example.product.infrastructure.persistence;

import com.example.product.domain.model.Product;
import com.example.product.domain.repository.ProductRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaProductRepository extends JpaRepository<Product, UUID>, ProductRepository {
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Product> findActiveById(@Param("id") UUID id);
}
