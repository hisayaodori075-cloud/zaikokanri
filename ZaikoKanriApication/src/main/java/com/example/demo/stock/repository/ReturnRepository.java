package com.example.demo.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.stock.entity.ReturnEntity;

public interface ReturnRepository extends JpaRepository<ReturnEntity, Integer> {

    // 商品ごとの返品一覧（削除されていないもの）
    List<ReturnEntity> findByProductIdAndDeleteFlag(Integer productId, Integer deleteFlag);

    // 商品ごとの返品合計（在庫計算用）
    @Query("SELECT COALESCE(SUM(r.returnQuantity), 0) " +
           "FROM ReturnEntity r " +
           "WHERE r.productId = :productId AND r.deleteFlag = 0")
    Integer sumReturnQuantityByProductId(@Param("productId") Integer productId);
}