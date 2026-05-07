package com.example.demo.stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.stock.entity.ReturnEntity;

@Repository
public interface ReturnRepository extends JpaRepository<ReturnEntity, Integer> {

    // ===============================
    // 基本取得
    // ===============================

    // 論理削除されていない返品データを取得
    List<ReturnEntity> findByDeletedFalse();

    Optional<ReturnEntity> findByIdAndDeletedFalse(Integer id);

    List<ReturnEntity> findByIdInAndDeletedFalse(List<Integer> ids);

    List<ReturnEntity> findByProductIdAndDeletedFalse(Integer productId);

    // ===============================
    // ★並び替え用（追加）
    // ===============================

    // ID順
    List<ReturnEntity> findByDeletedFalseOrderByIdAsc();

    List<ReturnEntity> findByDeletedFalseOrderByIdDesc();

    // 返品日順
    List<ReturnEntity> findByDeletedFalseOrderByReturnDateAsc();

    List<ReturnEntity> findByDeletedFalseOrderByReturnDateDesc();
}