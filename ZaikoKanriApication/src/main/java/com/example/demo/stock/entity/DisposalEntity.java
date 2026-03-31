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
@Table(name = "disposal")
public class DisposalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 対象商品ID
    @Column(name = "product_id", nullable = false)
    private Integer productId;

    // 廃棄数
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // 廃棄日
    @Column(name = "disposal_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate disposalDate;
    
    // 廃棄理由
    @Column(name = "reason")
    private String reason;

    // 論理削除用（将来必要になれば）
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    

    // ===== getter =====
    public Integer getId() { return id; }
    public Integer getProductId() { return productId; }
    public Integer getQuantity() { return quantity; }
    public LocalDate getDisposalDate() { return disposalDate; }
    public boolean isDeleted() { return deleted; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public String getReason() { return reason; }
    

    // ===== setter =====
    public void setId(Integer id) { this.id = id; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setDisposalDate(LocalDate disposalDate) { this.disposalDate = disposalDate; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public void setReason(String reason) { this.reason = reason; }
}