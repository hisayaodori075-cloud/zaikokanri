package com.example.demo.stock.repository;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.stock.entity.StockInEntity;

public interface StockInRepository extends JpaRepository<StockInEntity, Integer> {
    
    // ===============================
    // 論理削除
    // ===============================
    @Modifying
    @Transactional
    @Query("UPDATE StockInEntity s SET s.deleted = true, s.deletedAt = CURRENT_TIMESTAMP WHERE s.id = :id")
    void logicallyDeleteById(@Param("id") Integer id);

    // ===============================
    // 論理削除されていないもの取得
    // ===============================
    List<StockInEntity> findByDeletedFalse();

    List<StockInEntity> findByDeletedFalseOrderByArrivalDateDesc();

    // ===============================
    // ★追加（廃棄と統一）
    // ===============================

    // ID検索（論理削除除外）
    Optional<StockInEntity> findByIdAndDeletedFalse(Integer id);

    // 複数ID検索（論理削除除外）
    List<StockInEntity> findByIdInAndDeletedFalse(List<Integer> ids);
}