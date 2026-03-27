package com.example.demo.stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.stock.entity.ReturnEntity;

@Repository
public interface ReturnRepository extends JpaRepository<ReturnEntity, Integer> {

    // 論理削除されていない返品データを取得
    List<ReturnEntity> findByDeletedFalse();

    // ID検索（Optionalを使う場合は不要ですが、明示的に書くことも可能）
    Optional<ReturnEntity> findByIdAndDeletedFalse(Integer id);

    // 複数ID検索（論理削除されていないものだけ）
    List<ReturnEntity> findByIdInAndDeletedFalse(List<Integer> ids);

    // 商品IDで検索する場合の例
    List<ReturnEntity> findByProductIdAndDeletedFalse(Integer productId);
}