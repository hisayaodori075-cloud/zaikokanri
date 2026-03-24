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

    @Query("""
            SELECT p FROM ProductEntity p
            WHERE p.deleted = false
            AND (:janCode IS NULL OR p.janCode = :janCode)
            AND (:makerName IS NULL OR p.makerName LIKE CONCAT(:makerName, '%'))
            AND (:productName IS NULL OR p.productName LIKE CONCAT(:productName, '%'))
            AND (:purchasePrice IS NULL OR p.PurchasePrice = :purchasePrice)
            AND (:price IS NULL OR p.price = :price)
            AND (:salesStatus IS NULL OR p.salesStatus = :salesStatus)
            """)
    List<ProductEntity> search(
        @Param("janCode") String janCode,
        @Param("makerName") String makerName,
        @Param("productName") String productName,
        @Param("purchasePrice") Integer purchasePrice,
        @Param("price") Integer price,
        @Param("salesStatus") String salesStatus
    );

    @Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.deleted = true, p.deletedAt = CURRENT_TIMESTAMP WHERE p.id = :id")
    void logicallyDeleteById(Integer id);

    // 削除されていない商品一覧
    List<ProductEntity> findByDeletedFalse();

    // 商品名部分一致（論理削除除外）
    List<ProductEntity> findByProductNameContainingAndDeletedFalse(String productName);

    // JANコード + 商品名 完全一致（論理削除除外）
    List<ProductEntity> findByJanCodeAndProductNameAndDeletedFalse(String janCode, String productName);
}