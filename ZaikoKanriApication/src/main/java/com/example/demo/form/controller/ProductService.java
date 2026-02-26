package com.example.demo.form.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.form.ProductSearchForm;
import com.example.demo.menu.MenuProductRepository;
import com.example.demo.product.ProductEntity;

@Service
public class ProductService {

    @Autowired
    private MenuProductRepository productRepository;

    public List<ProductEntity> search(ProductSearchForm form) {

        // nullチェック（検索画面では重要）
        if (form.getJan() == null) {
            return productRepository.findAll();
        }

        // Integer → String に変換
        return productRepository.findByJanCode(String.valueOf(form.getJan()));
    }
}