package com.example.demo.stock.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.repository.ProductRepository;
import com.example.demo.stock.entity.ReturnEntity;
import com.example.demo.stock.repository.ReturnRepository;

@Service
public class ReturnService {

    @Autowired
    private ReturnRepository returnRepository;

    @Autowired
    private ProductRepository productRepository;

    public ReturnEntity findById(Integer id) {
        return returnRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    public List<ReturnEntity> findAllNotDeleted() {
        return returnRepository.findByDeletedFalse();
    }

    // ★返品登録＋在庫更新
    public boolean executeReturn(ReturnEntity returnData) {

        if (returnData.getCreatedAt() == null) {
            returnData.setCreatedAt(java.time.LocalDateTime.now());
        }

        ProductEntity product =
            productRepository.findById(returnData.getProductId()).orElse(null);

        if (product == null) return false;

        int stock = product.getStock() == null ? 0 : product.getStock();
        int qty = returnData.getReturnQuantity() == null ? 0 : returnData.getReturnQuantity();

        // ★最終防衛：在庫チェック
        if (qty > stock) {
            return false;
        }

        // ★最終防衛：数量チェック
        if (qty <= 0) {
            return false;
        }

        // 保存
        returnRepository.save(returnData);

        // 在庫減算
        product.setStock(stock - qty);
        productRepository.save(product);

        return true;
    }

    // ★返品削除＋在庫戻し
    public boolean executeDelete(Integer id) {

        ReturnEntity data =
            returnRepository.findByIdAndDeletedFalse(id).orElse(null);

        if (data == null) return false;

        // ★7日制限（ここ追加）
        if (data.getCreatedAt() != null &&
            data.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusDays(7))) {
            return false;
        }

        data.setDeleted(true);

        ProductEntity product =
            productRepository.findById(data.getProductId()).orElse(null);

        if (product != null) {
            int stock = product.getStock() == null ? 0 : product.getStock();
            int qty = data.getReturnQuantity() == null ? 0 : data.getReturnQuantity();

            product.setStock(stock + qty);
            productRepository.save(product);
        }

        returnRepository.save(data);

        return true;
    }
}