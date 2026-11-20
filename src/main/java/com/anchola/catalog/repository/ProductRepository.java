package com.anchola.catalog.repository;

import com.anchola.catalog.entity.Product;
import com.anchola.catalog.entity.ProductStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Optional<Product> findBySku(String sku);

    boolean existsByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.status = :status")
    List<Product> findByStatus(@Param("status") ProductStatus status);
}
