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

        String janCode = emptyToNull(form.getJan());
        String makerName = emptyToNull(form.getMakerName());
        String productName = emptyToNull(form.getName());
        Integer price = form.getPrice();
        String salesStatus = emptyToNull(form.getSalesStatus());

        return productRepository.search(
                janCode,
                makerName,
                productName,
                price,
                salesStatus
        );
    }

    private String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}