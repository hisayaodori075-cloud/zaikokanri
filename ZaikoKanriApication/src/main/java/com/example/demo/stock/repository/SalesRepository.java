package com.example.demo.stock.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // ★これも忘れず追加

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.stock.entity.SalesEntity;

@Repository
public interface SalesRepository extends JpaRepository<SalesEntity, Integer> {

    // 商品IDで販売履歴を検索（論理削除を除く）
    List<SalesEntity> findByProductIdAndDeletedFalse(Integer productId);

    // 論理削除
    @Modifying
    @Transactional
    @Query("""
           UPDATE SalesEntity s
           SET s.deleted = true,
               s.deletedAt = CURRENT_TIMESTAMP
           WHERE s.id = :id
           """)
    void logicallyDeleteById(@Param("id") Integer id);

    // 削除されていないものだけ取得
    List<SalesEntity> findByDeletedFalse();

    // ★追加：ID検索（論理削除除外）
    Optional<SalesEntity> findByIdAndDeletedFalse(Integer id);

    // -----------------------------
    // 販売履歴一覧（新しい順）
    // -----------------------------
    List<SalesEntity> findByDeletedFalseOrderBySalesDateDesc();

    // 指定日以降の販売数合計
    @Query("""
           SELECT COALESCE(SUM(s.salesQuantity),0)
           FROM SalesEntity s
           WHERE s.productId = :productId
           AND s.salesDate >= :startDate
           AND s.deleted = false
           """)
    Integer getSalesCountSince(
            @Param("productId") Integer productId,
            @Param("startDate") LocalDate startDate
    );

    // 最終販売日
    @Query("""
           SELECT MAX(s.salesDate)
           FROM SalesEntity s
           WHERE s.productId = :productId
           AND s.deleted = false
           """)
    LocalDate findLastSalesDate(@Param("productId") Integer productId);

}