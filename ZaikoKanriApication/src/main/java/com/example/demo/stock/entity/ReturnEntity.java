package com.example.demo.stock.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "returns")
public class ReturnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 対象商品ID
    @Column(name = "product_id", nullable = false)
    private Integer productId;

    // 返品数
    @Column(name = "return_quantity", nullable = false)
    private Integer returnQuantity;

    // 返品日
    @Column(name = "return_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
    
    // 廃棄理由
    @Column(name = "reason")
    private String reason;

    // 論理削除用
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ===== getter =====
    public Integer getId() { return id; }
    public Integer getProductId() { return productId; }
    public Integer getReturnQuantity() { return returnQuantity; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isDeleted() { return deleted; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public String getReason() { return reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ===== setter =====
    public void setId(Integer id) { this.id = id; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public void setReturnQuantity(Integer returnQuantity) { this.returnQuantity = returnQuantity; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public void setReason(String reason) { this.reason = reason; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}