package com.example.demo.dto;

public class ProductDto {

	private int jan;
    private String MakerName;   
	private String name;
	private int PurchasePrice;
    private int price;
    private String SalesStatus;
    
    // getter
    public int getjan() {
        return jan;
    }
    
    public String getMakerName() {
        return MakerName;
    }
    
    public String getName() {
        return name;
    }
    
    public int getPurchasePrice() {
        return PurchasePrice;
    }

    public int getPrice() {
        return price;
    }

    public String SalesStatus() {
        return SalesStatus;
    }

    // setter
    public void setjan(int jan) {
    		this.jan = jan;
    }
    
    public void setMakerName(String MakerName) {
    		this.MakerName = MakerName;
        
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPurchasePrice(int PurchasePrice) {
        this.PurchasePrice = PurchasePrice;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setSalesStatus(String SalesStatus) {
        this.SalesStatus = SalesStatus;
    }
}

