package com.example.demo.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "jan_code")
    private String janCode;

    @Column(name = "maker_name")
    private String makerName;

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
        return status;
    }

    public String getSalesStatus() {
        return salesStatus;
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
}