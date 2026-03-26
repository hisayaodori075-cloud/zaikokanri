package com.example.demo.stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.stock.entity.DisposalEntity;

@Repository
public interface DisposalRepository extends JpaRepository<DisposalEntity, Integer> {

    // 論理削除されていない廃棄データを取得
    List<DisposalEntity> findByDeletedFalse();

    // ID検索（Optionalを使う場合は不要ですが、明示的に書くことも可能）
    Optional<DisposalEntity> findByIdAndDeletedFalse(Integer id);

    // 複数ID検索（論理削除されていないものだけ）
    List<DisposalEntity> findByIdInAndDeletedFalse(List<Integer> ids);

    // 商品IDで検索する場合の例
    List<DisposalEntity> findByProductIdAndDeletedFalse(Integer productId);
}