package com.example.demo.stock.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.repository.ProductRepository;
import com.example.demo.stock.entity.DisposalEntity;
import com.example.demo.stock.repository.DisposalRepository;

@Service
public class DisposalService {

    @Autowired
    private DisposalRepository disposalRepository;

    // ★追加（在庫操作用）
    @Autowired
    private ProductRepository productRepository;

    // すべて取得（論理削除されていないものだけ）
    public List<DisposalEntity> findAll() {
        return disposalRepository.findByDeletedFalse();
    }

    // IDで取得（nullの場合はOptional.empty、論理削除されていないものだけ）
    public Optional<DisposalEntity> findOptionalById(Integer id) {
        return disposalRepository.findByIdAndDeletedFalse(id);
    }

    // IDで取得（null許容版、従来のメソッド、論理削除対応）
    public DisposalEntity findById(Integer id) {
        return disposalRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    // 保存（新規 or 更新）
    public DisposalEntity save(DisposalEntity disposal) {
        return disposalRepository.save(disposal);
    }

    // ★追加：廃棄＋在庫更新（Controllerから呼ぶ用）
    public void executeDisposal(DisposalEntity disposal) {

        // ① 廃棄保存
        disposalRepository.save(disposal);

        // ② 商品取得
        ProductEntity product =
                productRepository.findById(disposal.getProductId()).orElse(null);

        if (product == null) return;

        // ③ 在庫取得（null対策）
        Integer currentStock = product.getStock();
        if (currentStock == null) currentStock = 0;

        // ④ 廃棄数取得（null対策）
        Integer quantity = disposal.getQuantity();
        if (quantity == null) quantity = 0;

        // ⑤ 在庫減算
        product.setStock(currentStock - quantity);

        // ⑥ 更新
        productRepository.save(product);
    }

    // ★追加：廃棄削除＋在庫戻し
    public void executeDelete(Integer id) {

        DisposalEntity disposal =
            disposalRepository.findByIdAndDeletedFalse(id).orElse(null);

        if (disposal == null) return;

        // 論理削除
        disposal.setDeleted(true);

        ProductEntity product =
            productRepository.findById(disposal.getProductId()).orElse(null);

        if (product != null) {

            Integer currentStock = product.getStock();
            if (currentStock == null) currentStock = 0;

            Integer quantity = disposal.getQuantity();
            if (quantity == null) quantity = 0;

            // 在庫戻し（ここが重要）
            product.setStock(currentStock + quantity);

            productRepository.save(product);
        }

        // 最後に保存
        disposalRepository.save(disposal);
    }
    
    // ★追加：廃棄編集＋在庫更新（計算のみ）
    public boolean executeEdit(DisposalEntity newDisposal) {

        // ① 元データ取得
        DisposalEntity old =
            disposalRepository.findByIdAndDeletedFalse(newDisposal.getId())
                .orElse(null);

        if (old == null) return false;

        // ② 商品取得
        ProductEntity product =
            productRepository.findById(newDisposal.getProductId())
                .orElse(null);

        if (product == null) return false;

        // ③ 在庫取得
        int currentStock = product.getStock() == null ? 0 : product.getStock();

        // ④ 数量取得
        int oldQty = old.getQuantity() == null ? 0 : old.getQuantity();
        int newQty = newDisposal.getQuantity() == null ? 0 : newDisposal.getQuantity();

        // ⑤ 差分計算
        int diff = newQty - oldQty;

        // ⑥ 在庫反映
        int newStock = currentStock - diff;

        // ❗在庫不足はfalseで返す（例外禁止）
        if (newStock < 0) {
            return false;
        }

        product.setStock(newStock);

        // ⑦ 保存
        productRepository.save(product);
        disposalRepository.save(newDisposal);

        return true;
    }

    // 複数ID検索（論理削除されていないものだけ）
    public List<DisposalEntity> findByIds(List<Integer> ids) {
        return disposalRepository.findByIdInAndDeletedFalse(ids);
    }
    
    public List<DisposalEntity> findAllNotDeleted() {
        return disposalRepository.findByDeletedFalse();
    }
}