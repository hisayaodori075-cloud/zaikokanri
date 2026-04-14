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
        Integer purchasePrice = form.getPurchasePrice();
        Integer price = form.getPrice();        
        String salesStatus = emptyToNull(form.getSalesStatus());

        return productRepository.search(
                janCode,
                makerName,
                productName,
                purchasePrice,
                price,
                salesStatus
        );
    }

    private String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    // ---------------- 基本 CRUD ----------------
    public ProductEntity findById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    // ★追加（論理削除された商品を除外）
    public ProductEntity findByIdAndDeletedFalse(Integer id) {

        ProductEntity product = productRepository.findById(id).orElse(null);

        if (product == null || product.isDeleted()) {
            return null;
        }

        return product;
    }

    public List<ProductEntity> findAll() {
        return productRepository.findByDeletedFalse();
    }

    public void save(ProductEntity product) {

        // ★新規登録時のみ作成日をセット
        if (product.getId() == null) {
            product.setCreatedAt(java.time.LocalDateTime.now());
        }

        productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        productRepository.logicallyDeleteById(id);
    }

    // JANコード検索（空白対策）
    public ProductEntity findByJanCode(String janCode) {

        if (janCode == null || janCode.isBlank()) {
            return null;
        }

        janCode = janCode.trim();

        return productRepository.findByJanCodeAndDeletedFalse(janCode);
    }

    // 商品名部分一致（論理削除除外）
    public List<ProductEntity> findByProductNameContaining(String productName){
        return productRepository.findByProductNameContainingAndDeletedFalse(productName);
    }

    // ---------------- 在庫管理用検索 ----------------
    public List<ProductEntity> findByJanOrName(String janCode, String productName) {

        boolean janEmpty = (janCode == null || janCode.isBlank());
        boolean nameEmpty = (productName == null || productName.isBlank());

        // 両方未入力 → 全件
        if (janEmpty && nameEmpty) {
            return findAll();
        }

        // JANのみ
        if (!janEmpty && nameEmpty) {
            ProductEntity product = findByJanCode(janCode);
            return (product == null) ? List.of() : List.of(product);
        }

        // 商品名のみ
        if (janEmpty && !nameEmpty) {
            return findByProductNameContaining(productName);
        }

        // JAN + 商品名
        ProductEntity product = findByJanCode(janCode);

        if (product != null && product.getProductName().equals(productName)) {
            return List.of(product);
        }

        return List.of();
    }
    
    public boolean isJanCodeDuplicate(String janCode) {

        if (janCode == null || janCode.isBlank()) return false;

        return productRepository.existsByJanCodeAndDeletedFalse(janCode.trim());
    }

    // 編集用（自分以外）
    public boolean isJanCodeDuplicateForUpdate(String janCode, Integer id) {

        if (janCode == null || janCode.isBlank()) return false;

        return productRepository
            .existsByJanCodeAndDeletedFalseAndIdNot(janCode.trim(), id);
    }
}