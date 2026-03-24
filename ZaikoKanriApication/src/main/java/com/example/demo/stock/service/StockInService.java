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

    // 入荷保存 + 在庫更新（新規入荷）
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

    // ===============================
    // 入荷編集（追加）
    // ===============================
    public void update(StockInEntity stock) {

        // 元の入荷データ取得
        StockInEntity oldStock =
                stockInRepository.findById(stock.getId()).orElse(null);

        if (oldStock == null) {
            return;
        }

        ProductEntity product =
                productRepository.findById(stock.getProductId()).orElse(null);

        if (product != null) {

            Integer currentStock = product.getStock();

            if (currentStock == null) {
                currentStock = 0;
            }

            // 在庫計算
            int newStock =
                    currentStock
                    - oldStock.getQuantity()
                    + stock.getQuantity();

            product.setStock(newStock);

            // 更新
            productRepository.save(product);
            stockInRepository.save(stock);
        }
    }

    // 入荷ID検索（論理削除チェック付き）
    public StockInEntity findById(Integer id) {
        StockInEntity stock = stockInRepository.findById(id).orElse(null);
        if (stock == null || stock.isDeleted()) {
            return null;
        }
        return stock;
    }

    // 商品ID検索（論理削除チェック付き）
    public ProductEntity findProductById(Integer id) {
        ProductEntity product = productRepository.findById(id).orElse(null);
        if (product == null || product.isDeleted()) {
            return null;
        }
        return product;
    }

    // ===============================
    // 論理削除（在庫調整付き）
    // ===============================
    public void delete(Integer id) {

        StockInEntity stock =
                stockInRepository.findById(id).orElse(null);

        if (stock == null) {
            return;
        }

        ProductEntity product =
                productRepository.findById(stock.getProductId()).orElse(null);

        if (product != null) {

            Integer currentStock = product.getStock();

            if (currentStock == null) {
                currentStock = 0;
            }

            // 在庫を戻す
            product.setStock(currentStock - stock.getQuantity());

            productRepository.save(product);
        }

        stockInRepository.logicallyDeleteById(id);
    }

    // 論理削除されていないものだけ取得
    public List<StockInEntity> findAll() {
        return stockInRepository.findByDeletedFalse();
    }
}