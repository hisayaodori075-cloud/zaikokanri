package com.example.demo.stock.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.stock.entity.StockInEntity;

public interface StockRepository extends JpaRepository<StockInEntity, Integer> {
	
	// 論理削除
    @Modifying
    @Transactional
    @Query("UPDATE StockInEntity s SET s.deleted = true, s.deletedAt = CURRENT_TIMESTAMP WHERE s.id = :id")
    void logicallyDeleteById(@Param("id") Integer id);

    // 削除されていないものだけ取得
    List<StockInEntity> findByDeletedFalse();
}

