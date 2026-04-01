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
    public SalesEntity save(SalesEntity sales) {

        SalesEntity saved = salesRepository.save(sales);

        ProductEntity product =
                productRepository.findById(sales.getProductId()).orElse(null);

        if (product != null) {

            Integer stock = product.getStock();
            if (stock == null) stock = 0;

            // 新規はそのまま減算
            product.setStock(stock - sales.getSalesQuantity());

            productRepository.save(product);
        }

        return saved;
    }

    // -----------------------------
    // ★追加：販売情報更新（差分調整）
    // -----------------------------
    public SalesEntity update(SalesEntity sales) {

        // ① 旧データ取得
        SalesEntity old = salesRepository.findById(sales.getId()).orElse(null);
        if (old == null) {
            return null;
        }

        // ③ 商品取得（先に取る方が安全）
        ProductEntity product =
                productRepository.findById(sales.getProductId()).orElse(null);

        if (product != null) {

            Integer stock = product.getStock();
            if (stock == null) stock = 0;

            // ④ 差分計算（null対策込み）
            Integer newQty = sales.getSalesQuantity();
            Integer oldQty = old.getSalesQuantity();

            if (newQty == null) newQty = 0;
            if (oldQty == null) oldQty = 0;

            int diff = newQty - oldQty;

            // ⑤ 在庫調整（差分方式）
            product.setStock(stock - diff);

            productRepository.save(product);
        }

        // ② 更新保存（最後の方が安全）
        SalesEntity saved = salesRepository.save(sales);

        return saved;
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
    public void delete(Integer id) {

        // ① 販売データ取得
        SalesEntity sales = salesRepository.findById(id).orElse(null);
        if (sales == null || sales.isDeleted()) {
            return;
        }

        // ② 商品取得
        ProductEntity product =
                productRepository.findById(sales.getProductId()).orElse(null);

        if (product != null) {

            Integer stock = product.getStock();
            if (stock == null) stock = 0;

            // ③ 在庫を戻す（ここがポイント）
            product.setStock(stock + sales.getSalesQuantity());

            productRepository.save(product);
        }

        // ④ 論理削除
        sales.setDeleted(true);
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