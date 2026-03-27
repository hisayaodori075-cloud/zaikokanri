package com.example.demo.stock.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "returns")
public class ReturnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "return_quantity", nullable = false)
    private Integer returnQuantity;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "delete_flag")
    private Integer deleteFlag;

    // ===== Getter =====

    public Integer getId() {
        return id;
    }

    public Integer getProductId() {
        return productId;
    }

    public Integer getReturnQuantity() {
        return returnQuantity;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    // ===== Setter =====

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setReturnQuantity(Integer returnQuantity) {
        this.returnQuantity = returnQuantity;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}