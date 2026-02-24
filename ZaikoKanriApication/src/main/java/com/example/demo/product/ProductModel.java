package com.example.demo.product;

public class ProductModel {

    private String jan;
    private String MakerName;   
	private String name;
	private int PurchasePrice;
    private int price;
    private String Status;
    private String SalesStatus;
    

    // コンストラクタ（空）
    public ProductModel() {
    }

    // getter
    public String getJan() {
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
    
    public String getStatus() {
		return Status;
	}

    public String getSalesStatus() {
        return SalesStatus;
    }

    // setter
    public void setJan(String jan) {
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
    
    public void setStatus(String status) {
		Status = status;
	}

    public void setSalesStatus(String SalesStatus) {
        this.SalesStatus = SalesStatus;
    }

	

	
}

