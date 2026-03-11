package com.example.demo.stock.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.stock.entity.SalesEntity;
import com.example.demo.stock.repository.SalesRepository;

@Service
public class SalesService {

	@Autowired
    private SalesRepository salesRepository;

    // 販売情報を保存
    public SalesEntity save(SalesEntity sales) {
        return salesRepository.save(sales);
    }

    // 商品IDで検索
    public List<SalesEntity> findByProductId(Integer productId) {
        return salesRepository.findByProductId(productId);
    }
    
    public SalesEntity findById(Integer id) {
        return salesRepository.findById(id).orElse(null);
    }
    
    // 論理削除用
    public void delete(Integer id) {
        salesRepository.logicallyDeleteById(id); // ← 物理削除ではなく論理削除を呼ぶ
    }
    
    public List<SalesEntity> findAll() {
        return salesRepository.findByDeletedFalse(); // 論理削除済みを除外
    }
}