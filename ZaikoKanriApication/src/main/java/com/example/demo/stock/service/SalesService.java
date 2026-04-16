package com.example.demo.stock.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        // ① createdAt
        if (sales.getCreatedAt() == null) {
            sales.setCreatedAt(LocalDateTime.now());
        }

        // ② 商品取得
        ProductEntity product =
                productRepository.findById(sales.getProductId()).orElse(null);

        if (product == null) return false;

        // ③ 在庫取得（StockInと統一）
        int currentStock = product.getStock() == null ? 0 : product.getStock();

        // ④ 数量取得（StockInと統一）
        int qty = sales.getSalesQuantity() == null ? 0 : sales.getSalesQuantity();

        // ⑤ 数量チェック（StockInと統一）
        if (qty <= 0) return false;

        // ⑥ 在庫チェック
        if (qty > currentStock) return false;

        // ⑦ 在庫更新
        product.setStock(currentStock - qty);

        // ⑧ 保存
        salesRepository.save(sales);
        productRepository.save(product);

        return true;
    }

    public boolean canEdit(SalesEntity sales) {

        SalesEntity old =
            salesRepository.findByIdAndDeletedFalse(sales.getId())
                .orElse(null);

        if (old == null) return false;

        ProductEntity product =
            productRepository.findById(sales.getProductId())
                .orElse(null);

        if (product == null) return false;

        int currentStock = product.getStock() == null ? 0 : product.getStock();
        int oldQty = old.getSalesQuantity() == null ? 0 : old.getSalesQuantity();
        int newQty = sales.getSalesQuantity() == null ? 0 : sales.getSalesQuantity();

        if (newQty <= 0) return false;

        int newStock = currentStock + oldQty - newQty;

        return newStock >= 0;
    }
    
    public void update(SalesEntity sales) {

        SalesEntity old =
            salesRepository.findById(sales.getId()).orElse(null);

        if (old == null) return;

        // ★ createdAt維持（超重要）
        sales.setCreatedAt(old.getCreatedAt());

        ProductEntity product =
            productRepository.findById(sales.getProductId()).orElse(null);

        if (product == null) return;

        int currentStock = product.getStock() == null ? 0 : product.getStock();
        int oldQty = old.getSalesQuantity() == null ? 0 : old.getSalesQuantity();
        int newQty = sales.getSalesQuantity() == null ? 0 : sales.getSalesQuantity();

        if (newQty <= 0) return;

        int newStock = currentStock + oldQty - newQty;

        product.setStock(newStock);

        productRepository.save(product);
        salesRepository.save(sales);
    }
    
 // -----------------------------
 // 論理削除（統一版）
 // -----------------------------
 public boolean executeDelete(Integer id) {

     // ① データ取得（論理削除除外）
     SalesEntity sales = salesRepository.findById(id).orElse(null);

     if (sales == null || sales.isDeleted()) return false;

     // ★② 7日制限チェック
     if (sales.getCreatedAt() != null &&
         sales.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusDays(7))) {
         return false;
     }

     // ③ 商品取得
     ProductEntity product =
         productRepository.findById(sales.getProductId()).orElse(null);

     if (product == null) return false;

     // ④ 在庫計算（戻す）
     int stock = product.getStock() == null ? 0 : product.getStock();
     int qty = sales.getSalesQuantity() == null ? 0 : sales.getSalesQuantity();

     int newStock = stock + qty;

     product.setStock(newStock);
     productRepository.save(product);

     // ⑤ 論理削除
     sales.setDeleted(true);
     sales.setDeletedAt(java.time.LocalDateTime.now());
     salesRepository.save(sales);

     return true;
 }
 	
	//===============================
	// ★取得系（廃棄と完全統一）
	// ===============================
 
	 //-----------------------------
	 // 全件取得
	 // -----------------------------
	 public List<SalesEntity> findAll() {
	     return salesRepository.findByDeletedFalse();
	 }
 
 	//Optional取得
	 public Optional<SalesEntity> findOptionalById(Integer id) {
		    return salesRepository.findById(id)
		            .filter(s -> !s.isDeleted());
	}
	 
	
	// -----------------------------
	// null許容取得
	// -----------------------------
	 public SalesEntity findById(Integer id) {
		    return salesRepository.findById(id)
		            .filter(s -> !s.isDeleted())
		            .orElse(null);
		}
	 
	
	// 複数取得
	 public List<SalesEntity> findByIds(List<Integer> ids) {
		    return salesRepository.findAllById(ids).stream()
		            .filter(s -> !s.isDeleted())
		            .toList();
		}

    // -----------------------------
    // 商品取得（論理削除対応）
    // -----------------------------
	 public ProductEntity findProductById(Integer id) {
		    return productRepository.findById(id)
		            .filter(p -> !p.isDeleted())
		            .orElse(null);
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