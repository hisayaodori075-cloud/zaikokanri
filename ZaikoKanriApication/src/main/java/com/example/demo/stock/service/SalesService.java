package com.example.demo.stock.service;

import java.time.LocalDate;
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
    
    // 論理削除
    public void delete(Integer id) {
        salesRepository.logicallyDeleteById(id);
    }
    
    // 一覧取得（論理削除除外）
    public List<SalesEntity> findAll() {
        return salesRepository.findByDeletedFalse();
    }

    /**
     * 指定日数以内の販売数取得（回転率アラート用）
     */
    public int getSalesCountLastDays(Integer productId, int days) {

        LocalDate startDate = LocalDate.now().minusDays(days);

        Integer result = salesRepository.getSalesCountSince(productId, startDate);

        return result != null ? result : 0;
    }
}