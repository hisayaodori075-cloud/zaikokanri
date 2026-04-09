package com.example.demo.stock.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.repository.ProductRepository;
import com.example.demo.stock.entity.SalesEntity;
import com.example.demo.stock.repository.SalesRepository;

@Service
public class SalesService {

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private ProductRepository productRepository;

    // -----------------------------
    // 販売情報保存（新規）
    // -----------------------------
    public boolean executeSales(SalesEntity sales) {

        // ★① createdAtセット
        if (sales.getCreatedAt() == null) {
            sales.setCreatedAt(java.time.LocalDateTime.now());
        }

        // ② 商品取得
        ProductEntity product =
                productRepository.findById(sales.getProductId()).orElse(null);

        if (product == null) return false;

        // ③ 在庫取得
        Integer stock = product.getStock();
        if (stock == null) stock = 0;

        // ④ 数量取得
        Integer qty = sales.getSalesQuantity();
        if (qty == null) qty = 0;

        // ★⑤ 在庫チェック（先にやる）
        if (qty > stock) {
            return false;
        }

        // ⑥ 保存
        salesRepository.save(sales);

        // ⑦ 在庫減算
        product.setStock(stock - qty);

        productRepository.save(product);

        return true;
    }

    public boolean executeEdit(SalesEntity newSales) {

        SalesEntity old =
            salesRepository.findById(newSales.getId()).orElse(null);

        if (old == null || old.isDeleted()) return false;

        newSales.setCreatedAt(old.getCreatedAt());

        ProductEntity product =
            productRepository.findById(newSales.getProductId()).orElse(null);

        if (product == null) return false;

        int currentStock = product.getStock() == null ? 0 : product.getStock();

        int oldQty = old.getSalesQuantity() == null ? 0 : old.getSalesQuantity();
        int newQty = newSales.getSalesQuantity() == null ? 0 : newSales.getSalesQuantity();

        int newStock = currentStock + oldQty - newQty;

        if (newStock < 0) return false;

        product.setStock(newStock);

        productRepository.save(product);
        salesRepository.save(newSales);

        return true;
    }

    // -----------------------------
    // 商品IDで販売履歴取得（論理削除除外）
    // -----------------------------
    public List<SalesEntity> findByProductId(Integer productId) {
        return salesRepository.findByProductIdAndDeletedFalse(productId);
    }

    // -----------------------------
    // ID検索（論理削除チェック付き）
    // -----------------------------
    public SalesEntity findById(Integer id) {

        SalesEntity sales = salesRepository.findById(id).orElse(null);

        if (sales == null || sales.isDeleted()) {
            return null;
        }

        return sales;
    }

    // -----------------------------
    // 論理削除
    // -----------------------------
    public void executeDelete(Integer id) {

        // ① データ取得（論理削除除外）
    		SalesEntity sales = salesRepository.findById(id).orElse(null);

        if (sales == null) return;

        // ★② 7日制限チェック
        if (sales.getCreatedAt() != null &&
            sales.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusDays(7))) {
            return;
        }

        // ③ 商品取得
        ProductEntity product =
            productRepository.findById(sales.getProductId()).orElse(null);

        if (product != null) {

            Integer stock = product.getStock();
            if (stock == null) stock = 0;

            Integer qty = sales.getSalesQuantity();
            if (qty == null) qty = 0;

            // ④ 在庫戻し
            product.setStock(stock + qty);

            productRepository.save(product);
        }

        // ⑤ 論理削除
        sales.setDeleted(true);

        // （余裕あれば）
        sales.setDeletedAt(java.time.LocalDateTime.now());

        salesRepository.save(sales);
    }

    // -----------------------------
    // 一覧取得（論理削除除外）
    // -----------------------------
    public List<SalesEntity> findAll() {
        return salesRepository.findByDeletedFalse();
    }

    // -----------------------------
    // 指定日数以内の販売数取得（回転率用）
    // -----------------------------
    public int getSalesCountLastDays(Integer productId, int days) {

        LocalDate startDate = LocalDate.now().minusDays(days);

        Integer result = salesRepository.getSalesCountSince(productId, startDate);

        return result != null ? result : 0;
    }

    // -----------------------------
    // 販売履歴一覧（新しい順）
    // -----------------------------
    public List<SalesEntity> getSalesList() {
        return salesRepository.findByDeletedFalseOrderBySalesDateDesc();
    }
}