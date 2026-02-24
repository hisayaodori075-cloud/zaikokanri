package com.example.demo.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.product.ProductEntity;

public interface MenuProductRepository extends JpaRepository<ProductEntity, Integer> {
}
