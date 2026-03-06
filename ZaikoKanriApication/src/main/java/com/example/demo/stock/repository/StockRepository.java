package com.example.demo.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.stock.entity.StockInEntity;

public interface StockRepository extends JpaRepository<StockInEntity, Integer> {

}