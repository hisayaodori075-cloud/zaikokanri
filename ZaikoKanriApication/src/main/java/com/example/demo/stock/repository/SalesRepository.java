package com.example.demo.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.stock.entity.SalesEntity;

@Repository
public interface SalesRepository extends JpaRepository<SalesEntity, Integer> {

    // 商品IDで販売履歴を検索
    List<SalesEntity> findByProductId(Integer productId);
}