package com.example.demo.stock.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    // ===============================
    // 入荷登録（変更なし）
    // ===============================
    public boolean executeArrival(StockInEntity stock) {

        if (stock.getCreatedAt() == null) {
            stock.setCreatedAt(LocalDateTime.now());
        }

        ProductEntity product =
            productRepository.findById(stock.getProductId()).orElse(null);

        if (product == null) return false;

        int currentStock = product.getStock() == null ? 0 : product.getStock();
        int qty = stock.getQuantity() == null ? 0 : stock.getQuantity();

        if (qty <= 0) return false;

        product.setStock(currentStock + qty);
        LocalDateTime newArrival =
        	    stock.getArrivalDate().atStartOfDay();

        	LocalDateTime current = product.getLastArrivalDate();

        	if (current == null || newArrival.isAfter(current)) {
        	    product.setLastArrivalDate(newArrival);
        	}

        stockInRepository.save(stock);
        productRepository.save(product);

        return true;
    }

    // ===============================
    // 入荷編集（変更なし）
    // ===============================
    public void update(StockInEntity stock) {

        StockInEntity oldStock =
                stockInRepository.findById(stock.getId()).orElse(null);

        if (oldStock == null) return;

        // ★追加（これが今回のエラーの原因対策）
        stock.setCreatedAt(oldStock.getCreatedAt());

        ProductEntity product =
                productRepository.findById(stock.getProductId()).orElse(null);

        if (product == null) return;

        int currentStock = product.getStock() == null ? 0 : product.getStock();
        int oldQty = oldStock.getQuantity() == null ? 0 : oldStock.getQuantity();
        int newQty = stock.getQuantity() == null ? 0 : stock.getQuantity();

        if (newQty <= 0) return;

        int newStock = currentStock - oldQty + newQty;

        product.setStock(newStock);

        if (stock.getArrivalDate() != null) {

            LocalDateTime newArrival =
                stock.getArrivalDate().atStartOfDay();

            LocalDateTime current = product.getLastArrivalDate();

            if (current == null || newArrival.isAfter(current)) {
                product.setLastArrivalDate(newArrival);
            }
        }

        productRepository.save(product);
        stockInRepository.save(stock);
    }

    // ===============================
    // ★追加：編集チェック（Controllerから呼ぶ）
    // ===============================
    public boolean canEdit(StockInEntity stock) {

        StockInEntity old =
            stockInRepository.findByIdAndDeletedFalse(stock.getId())
                .orElse(null);

        if (old == null) return false;

        ProductEntity product =
            productRepository.findById(stock.getProductId())
                .orElse(null);

        if (product == null) return false;

        int currentStock = product.getStock() == null ? 0 : product.getStock();
        int oldQty = old.getQuantity() == null ? 0 : old.getQuantity();
        int newQty = stock.getQuantity() == null ? 0 : stock.getQuantity();

        if (newQty <= 0) return false;

        int newStock = currentStock - oldQty + newQty;

        return newStock >= 0;
    }

    // ===============================
    // 論理削除（変更なし）
    // ===============================
    public boolean executeDelete(Integer id) {

        StockInEntity stock =
                stockInRepository.findById(id).orElse(null);

        if (stock == null) return false;

        ProductEntity product =
                productRepository.findById(stock.getProductId()).orElse(null);

        if (product == null) return false;

        int currentStock = product.getStock() == null ? 0 : product.getStock();
        int qty = stock.getQuantity() == null ? 0 : stock.getQuantity();

        if (qty <= 0) return false;

        // ★削除後の在庫計算
        int newStock = currentStock - qty;

        // ★在庫不足チェック（ここが追加ポイント）
        if (newStock < 0) {
            return false; // ←削除させない（重要）
        }

        product.setStock(newStock);

        productRepository.save(product);

        stockInRepository.logicallyDeleteById(id);

        return true;
    }

    // ===============================
    // ★取得系（廃棄と完全統一）
    // ===============================

    // 全件取得
    public List<StockInEntity> findAll() {
        return stockInRepository.findByDeletedFalse();
    }

    // Optional取得
    public Optional<StockInEntity> findOptionalById(Integer id) {
        return stockInRepository.findByIdAndDeletedFalse(id);
    }

    // null許容取得
    public StockInEntity findById(Integer id) {
        return stockInRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    // 複数取得
    public List<StockInEntity> findByIds(List<Integer> ids) {
        return stockInRepository.findByIdInAndDeletedFalse(ids);
    }

    // 商品取得（論理削除対応）
    public ProductEntity findProductById(Integer id) {
        return productRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElse(null);
    }

    // ===============================
    // 一覧表示
    // ===============================
    public List<StockInEntity> getStockInList() {
        return stockInRepository.findByDeletedFalseOrderByArrivalDateDesc();
    }
}