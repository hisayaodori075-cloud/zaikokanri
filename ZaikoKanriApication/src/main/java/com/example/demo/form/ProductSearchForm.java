package com.example.demo.form;

public class ProductSearchForm {	
	    private Integer jan;
	    private String makerName;
	    private String name;
	    private Integer purchasePrice;
	    private Integer price;
	    private String salesStatus;
	    
	    
	    // jan
		public Integer getJan() {
			return jan;
		}
		public void setJan(Integer jan) {
			this.jan = jan;
		}
		
		// makerName
		public String getMakerName() {
			return makerName;
		}
		public void setMakerName(String makerName) {
			this.makerName = makerName;
		}
		
		// Name
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		// PurchasePrice
		public Integer getPurchasePrice() {
			return purchasePrice;
		}
		public void setPurchasePrice(Integer purchasePrice) {
			this.purchasePrice = purchasePrice;
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

