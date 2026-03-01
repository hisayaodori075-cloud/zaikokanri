package com.example.demo.form;

public class ProductSearchForm {	
	    private String janCode;
	    private String makerName;
	    private String productName;
	    private Integer PurchasePrice;
	    private Integer price;
	    private String salesStatus;
	    
	    
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
		public void setName(String productName) {
			this.productName = productName;
		}
		
		// PurchasePrice
		public Integer getPurchasePrice() {
			return PurchasePrice;
		}
		public void setPurchasePrice(Integer PurchasePrice) {
			this.PurchasePrice = PurchasePrice;
		}
		
		// Price
		public Integer getPrice() {
			return price;
		}
		public void setPrice(Integer price) {
			this.price = price;
		}
		
		// SalesStatus
		public String getSalesStatus() {
			return salesStatus;
		}
		
		public void setSalesStatus(String salesStatus) {
			this.salesStatus = salesStatus;
		}
	}

