package com.example.demo.product.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.product.entity.ProductEntity;

public interface ProductRepository 
        extends JpaRepository<ProductEntity, Integer> {

    // JANコード完全一致（論理削除除外）
    ProductEntity findByJanCodeAndDeletedFalse(String janCode);

    // ★追加（これが赤線解消）
    ProductEntity findByProductNameAndDeletedFalse(String productName);

    @Query("""
            SELECT p FROM ProductEntity p
            WHERE p.deleted = false
            AND (:janCode IS NULL OR p.janCode = :janCode)
            AND (:makerName IS NULL OR p.makerName LIKE CONCAT(:makerName, '%'))
            AND (:productName IS NULL OR p.productName LIKE CONCAT(:productName, '%'))

            AND (:purchasePriceMin IS NULL OR p.PurchasePrice >= :purchasePriceMin)
            AND (:purchasePriceMax IS NULL OR p.PurchasePrice <= :purchasePriceMax)

            AND (:priceMin IS NULL OR p.price >= :priceMin)
            AND (:priceMax IS NULL OR p.price <= :priceMax)

            AND (:salesStatus IS NULL OR p.salesStatus = :salesStatus)
            """)
    List<ProductEntity> search(
        @Param("janCode") String janCode,
        @Param("makerName") String makerName,
        @Param("productName") String productName,

        @Param("purchasePriceMin") Integer purchasePriceMin,
        @Param("purchasePriceMax") Integer purchasePriceMax,

        @Param("priceMin") Integer priceMin,
        @Param("priceMax") Integer priceMax,

        @Param("salesStatus") String salesStatus
    );

    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.deleted = true, p.deletedAt = CURRENT_TIMESTAMP WHERE p.id = :id")
    void logicallyDeleteById(Integer id);

    List<ProductEntity> findByDeletedFalse();

    List<ProductEntity> findByProductNameContainingAndDeletedFalse(String productName);

    List<ProductEntity> findByJanCodeAndProductNameAndDeletedFalse(String janCode, String productName);

    boolean existsByJanCodeAndDeletedFalse(String janCode);

    boolean existsByJanCodeAndDeletedFalseAndIdNot(String janCode, Integer id);
}