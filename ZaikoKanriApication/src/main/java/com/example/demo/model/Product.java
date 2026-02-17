package com.example.demo.model;

public class Product {

    private String name;
    private int price;
    private int stock;

    // コンストラクタ（空）
    public Product() {
    }

    // getter
    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    // setter
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}

