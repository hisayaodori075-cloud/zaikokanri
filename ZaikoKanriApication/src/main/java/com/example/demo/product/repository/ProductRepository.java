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
	ProductEntity findByJanCode(String janCode);

	@Query("""
		    SELECT p FROM ProductEntity p
		    WHERE (:janCode IS NULL OR p.janCode LIKE %:janCode%)
		      AND (:makerName IS NULL OR p.makerName LIKE %:makerName%)
		      AND (:productName IS NULL OR p.productName LIKE %:productName%)
		      AND (:price IS NULL OR p.price = :price)
		      AND (:salesStatus IS NULL OR p.salesStatus LIKE %:salesStatus%)
		""")
		List<ProductEntity> search(
		    @Param("janCode") String janCode,
		    @Param("makerName") String makerName,
		    @Param("productName") String productName,
		    @Param("price") Integer price,
		    @Param("salesStatus") String salesStatus
		);
	
	@Modifying
    @Transactional
    @Query("UPDATE ProductEntity p SET p.deleted = true, p.deletedAt = CURRENT_TIMESTAMP WHERE p.id = :id")
    void logicallyDeleteById(Integer id);

    // 削除されていないものだけ取得
    List<ProductEntity> findByDeletedFalse();
}