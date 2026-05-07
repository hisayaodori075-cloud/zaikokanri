package com.example.demo.stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.stock.entity.DisposalEntity;

@Repository
public interface DisposalRepository extends JpaRepository<DisposalEntity, Integer> {

    // ===============================
    // 論理削除されていない廃棄データ
    // ===============================
    List<DisposalEntity> findByDeletedFalse();

    Optional<DisposalEntity> findByIdAndDeletedFalse(Integer id);

    List<DisposalEntity> findByIdInAndDeletedFalse(List<Integer> ids);

    List<DisposalEntity> findByProductIdAndDeletedFalse(Integer productId);

    // ===============================
    // ★並び替え用（追加：入荷と統一）
    // ===============================

    // ID順
    List<DisposalEntity> findByDeletedFalseOrderByIdAsc();

    List<DisposalEntity> findByDeletedFalseOrderByIdDesc();

    // 廃棄日順
    List<DisposalEntity> findByDeletedFalseOrderByDisposalDateAsc();

    List<DisposalEntity> findByDeletedFalseOrderByDisposalDateDesc();
}