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

    // 入荷ID検索（論理削除チェック付き）
    public StockInEntity findById(Integer id) {
        StockInEntity stock = stockInRepository.findById(id).orElse(null);
        if (stock == null || stock.isDeleted()) {
            return null;  // 論理削除済みも存在しない扱い
        }
        return stock;
    }

    // 商品ID検索（論理削除チェック付き）
    public ProductEntity findProductById(Integer id) {
        ProductEntity product = productRepository.findById(id).orElse(null);
        if (product == null || product.isDeleted()) {
            return null;  // 論理削除済みは編集不可
        }
        return product;
    }

    // 論理削除
    public void delete(Integer id) {
        stockInRepository.logicallyDeleteById(id);
    }

    // 論理削除されていないものだけ取得
    public List<StockInEntity> findAll() {
        return stockInRepository.findByDeletedFalse();
    }
}