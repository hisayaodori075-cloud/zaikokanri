package com.example.demo.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.stock.entity.StockTakeEntity;

public interface StockTakeRepository extends JpaRepository<StockTakeEntity, Integer> {

    List<StockTakeEntity> findByDeletedFalse();
}