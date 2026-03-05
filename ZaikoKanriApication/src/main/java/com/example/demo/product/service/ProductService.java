package com.example.demo.product.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.form.ProductSearchForm;
import com.example.demo.product.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductEntity> search(ProductSearchForm form) {

        String janCode = emptyToNull(form.getJanCode());
        String makerName = emptyToNull(form.getMakerName());
        String productName = emptyToNull(form.getProductName());
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
    
    public ProductEntity findById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }
    
    public List<ProductEntity> findAll() {
        return productRepository.findAll();
    }

    public void save(ProductEntity product) {
        productRepository.save(product);
    }
    
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }
}