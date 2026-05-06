package com.example.demo.product.form;

public class ProductSearchForm {	
	    private String janCode;
	    private String makerName;
	    private String productName;
	    private Integer PurchasePrice;
	    private Integer price;
	    private String salesStatus;
	    
	    //検索範囲のための追加//
	    private Integer purchasePriceMin;
	    private Integer purchasePriceMax;
	    private Integer priceMin;
	    private Integer priceMax;
	    
	    
	    // jan
		public String getJanCode() {
			return janCode;
		}
		public void setJanCode(String janCode) {
			this.janCode = janCode;
		}
		
		// makerName
		public String getMakerName() {
			return makerName;
		}
		public void setMakerName(String makerName) {
			this.makerName = makerName;
		}
		
		// Name
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		
		// PurchasePrice
		public Integer getPurchasePrice() {
			return PurchasePrice;
		}
		public void setPurchasePrice(Integer PurchasePrice) {
			this.PurchasePrice = PurchasePrice;
		}
		
		// PurchasePrice（後から追加したほう）
		public Integer getPurchasePriceMin() {
		    return purchasePriceMin;
		}
		public void setPurchasePriceMin(Integer purchasePriceMin) {
		    this.purchasePriceMin = purchasePriceMin;
		}

		public Integer getPurchasePriceMax() {
		    return purchasePriceMax;
		}
		public void setPurchasePriceMax(Integer purchasePriceMax) {
		    this.purchasePriceMax = purchasePriceMax;
		}
		
		// Price
		public Integer getPrice() {
			return price;
		}
		public void setPrice(Integer price) {
			this.price = price;
		}
		
		//Price（後から追加したほう）
		public Integer getPriceMin() {
		    return priceMin;
		}
		public void setPriceMin(Integer priceMin) {
		    this.priceMin = priceMin;
		}

		public Integer getPriceMax() {
		    return priceMax;
		}
		public void setPriceMax(Integer priceMax) {
		    this.priceMax = priceMax;
		}
		
		// SalesStatus
		public String getSalesStatus() {
			return salesStatus;
		}
		
		public void setSalesStatus(String salesStatus) {
			this.salesStatus = salesStatus;
		}
	}

