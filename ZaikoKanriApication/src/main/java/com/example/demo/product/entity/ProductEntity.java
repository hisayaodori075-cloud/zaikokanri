package com.example.demo.product.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "jan_code")
    private String janCode;

    @Size(max = 30, message = "メーカー名は30文字以内で入力してください")
    @Column(name = "maker_name")
    private String makerName;

    @Size(max = 30, message = "商品名は30文字以内で入力してください")
    @Column(name = "product_name")
    private String productName;

    @Column(name = "purchase_price")
    private Integer PurchasePrice;
    
    @Column(name = "price")
    private Integer price;

    @Column(name = "status")
    private String status;

    @Column(name = "sales_status")
    private String salesStatus;
    
    @Column(name = "stock")
    private Integer stock;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "last_arrival_date")
    private LocalDateTime lastArrivalDate;
    
    // ---------------- 論理削除用 ----------------
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ===== getter =====
    public Integer getId() {
        return id;
    }

    public String getJanCode() {
        return janCode;
    }

    public String getMakerName() {
        return makerName;
    }

    public String getProductName() {
        return productName;
    }
    
    public Integer getPurchasePrice() {
        return PurchasePrice;
    }

    public Integer getPrice() {
        return price;
    }

    public String getStatus() {

        if (this.stock == null) {
            return "不明";
        }

        if (this.stock <= 5) {
            return "少ない";
        } 
        else if (this.stock <= 20) {
            return "適正";
        } 
        else {
            return "多い";
        }
    }

    public String getSalesStatus() {
        return salesStatus;
    }
    
    public boolean isDeleted() {
        return deleted;
    }
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getLastArrivalDate() {
        return lastArrivalDate;
    }

    // ===== setter =====
    public void setId(Integer id) {
        this.id = id;
    }

    public void setJanCode(String janCode) {
        this.janCode = janCode;
    }

    public void setMakerName(String makerName) {
        this.makerName = makerName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSalesStatus(String salesStatus) {
        this.salesStatus = salesStatus;
    }

    public void setPurchasePrice(Integer purchasePrice) {
        PurchasePrice = purchasePrice;
    }
    
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setLastArrivalDate(LocalDateTime lastArrivalDate) {
        this.lastArrivalDate = lastArrivalDate;
    }
}