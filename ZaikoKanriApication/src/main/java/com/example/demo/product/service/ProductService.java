package com.example.demo.product.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.form.ProductSearchForm;
import com.example.demo.product.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // ---------------- 通常検索 ----------------
    public List<ProductEntity> search(ProductSearchForm form) {
        String janCode = emptyToNull(form.getJanCode());
        String makerName = emptyToNull(form.getMakerName());
        String productName = emptyToNull(form.getProductName());
        Integer price = form.getPrice();
        String salesStatus = emptyToNull(form.getSalesStatus());

        return productRepository.search(
                janCode,
                makerName,
                productName,
                price,
                salesStatus
        );
    }

    private String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    // ---------------- 基本 CRUD ----------------
    public ProductEntity findById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<ProductEntity> findAll() {
        // 論理削除されていないものだけ取得
        return productRepository.findByDeletedFalse();
    }

    public void save(ProductEntity product) {
        productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        productRepository.logicallyDeleteById(id); // 論理削除
    }

    public ProductEntity findByJanCode(String janCode) {
        return productRepository.findByJanCode(janCode);
    }

    public List<ProductEntity> findByProductNameContaining(String productName){
        return productRepository.findByProductNameContaining(productName); // ← 変更
    }

    // ---------------- 在庫管理用検索 ----------------
    public List<ProductEntity> findByJanOrName(String janCode, String productName) {

        // 両方未入力 → 全件
        if ((janCode == null || janCode.isBlank()) && (productName == null || productName.isBlank())) {
            return findAll();
        }

        // JANコードのみ入力 → 単一商品をリスト化して返す
        else if (janCode != null && !janCode.isBlank() && (productName == null || productName.isBlank())) {
            ProductEntity product = findByJanCode(janCode);
            return (product == null) ? List.of() : List.of(product);
        }

        // 商品名のみ入力 → 部分検索
        else if ((janCode == null || janCode.isBlank()) && productName != null && !productName.isBlank()) {
            return findByProductNameContaining(productName);
        }

        // 両方入力 → 両条件で検索
        else {
            return productRepository.findByJanCodeAndProductNameContaining(janCode, productName); // ← 変更
        }
    }
}