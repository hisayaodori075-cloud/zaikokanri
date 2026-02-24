package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.product.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
}
