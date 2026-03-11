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
@Table(name = "sales")
public class SalesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productId;      // 商品ID
    private Integer salesQuantity;  // 販売数
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate salesDate;    // 販売日
    
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // getter / setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public Integer getSalesQuantity() { return salesQuantity; }
    public void setSalesQuantity(Integer salesQuantity) { this.salesQuantity = salesQuantity; }

    public LocalDate getSalesDate() { return salesDate; }
    public void setSalesDate(LocalDate salesDate) { this.salesDate = salesDate; }
    
    public boolean isDeleted() { return deleted; }
    
	public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}