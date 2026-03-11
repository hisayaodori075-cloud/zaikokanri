package com.example.demo.stock.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.stock.entity.SalesEntity;

@Repository
public interface SalesRepository extends JpaRepository<SalesEntity, Integer> {

    // 商品IDで販売履歴を検索
    List<SalesEntity> findByProductId(Integer productId);
    
    // 論理削除
    @Modifying
    @Transactional
    @Query("UPDATE SalesEntity s SET s.deleted = true, s.deletedAt = CURRENT_TIMESTAMP WHERE s.id = :id")
    void logicallyDeleteById(@Param("id") Integer id);

    // 削除されていないものだけ取得
    List<SalesEntity> findByDeletedFalse();
}