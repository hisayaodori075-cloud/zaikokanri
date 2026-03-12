package com.example.demo.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.stock.entity.DisposalEntity;

@Repository
public interface DisposalRepository extends JpaRepository<DisposalEntity, Integer> {
    // 追加で検索メソッドを作る場合はここに記述できます
    // 例：List<DisposalEntity> findByProductId(Integer productId);
}
