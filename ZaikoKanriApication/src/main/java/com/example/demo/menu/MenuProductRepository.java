package com.example.demo.menu;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.product.ProductEntity;

public interface MenuProductRepository 
        extends JpaRepository<ProductEntity, Integer> {

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
}