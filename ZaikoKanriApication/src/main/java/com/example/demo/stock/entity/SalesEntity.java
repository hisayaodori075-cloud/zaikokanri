package com.example.demo.stock.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sales")
public class SalesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productId;      // 商品ID
    private Integer salesQuantity;  // 販売数
    private LocalDate salesDate;    // 販売日

    // getter / setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public Integer getSalesQuantity() { return salesQuantity; }
    public void setSalesQuantity(Integer salesQuantity) { this.salesQuantity = salesQuantity; }

    public LocalDate getSalesDate() { return salesDate; }
    public void setSalesDate(LocalDate salesDate) { this.salesDate = salesDate; }
}