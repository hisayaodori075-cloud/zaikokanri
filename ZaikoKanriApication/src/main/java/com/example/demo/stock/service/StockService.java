package com.example.demo.stock.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.stock.entity.StockInEntity;
import com.example.demo.stock.repository.StockRepository;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public void save(StockInEntity stock) {
        stockRepository.save(stock);
    }
    
    public StockInEntity findById(Integer id) {
        return stockRepository.findById(id).orElse(null);
    }
    
 // 論理削除用
    public void delete(Integer id) {
        stockRepository.logicallyDeleteById(id); // ← 物理削除ではなく論理削除を呼ぶ
    }
    
    public List<StockInEntity> findAll() {
        return stockRepository.findByDeletedFalse(); // 論理削除済みを除外
    }
}
