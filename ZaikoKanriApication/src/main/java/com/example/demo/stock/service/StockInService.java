package com.example.demo.stock.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.repository.ProductRepository;
import com.example.demo.stock.entity.StockInEntity;
import com.example.demo.stock.repository.StockInRepository;

@Service
public class StockInService {

    @Autowired
    private StockInRepository stockInRepository;

    @Autowired
    private ProductRepository productRepository;

    // 入荷保存 + 在庫更新
    public void save(StockInEntity stock) {

        // ① 入荷履歴保存
        stockInRepository.save(stock);

        // ② 商品取得
        ProductEntity product =
                productRepository.findById(stock.getProductId()).orElse(null);

        if (product != null) {

            // ③ 現在の在庫取得
            Integer currentStock = product.getStock();

            if (currentStock == null) {
                currentStock = 0;
            }

            // ④ 在庫加算
            product.setStock(currentStock + stock.getQuantity());

            // ⑤ 商品更新
            productRepository.save(product);
        }
    }

    public StockInEntity findById(Integer id) {
        return stockInRepository.findById(id).orElse(null);
    }

    // 論理削除
    public void delete(Integer id) {
        stockInRepository.logicallyDeleteById(id);
    }

    public List<StockInEntity> findAll() {
        return stockInRepository.findByDeletedFalse();
    }
}