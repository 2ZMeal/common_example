package com.example.product.domain.repository;

import com.example.product.domain.model.Product;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);
    Optional<Product> findActiveById(UUID id);
    boolean existsById(UUID id);
}
