package com.example.demo.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.product.repository.ProductRepository;

@RequestMapping("/menu")
@Controller
public class MenuController {

    @Autowired
    private ProductRepository productRepository;

    // ログイン後画面
    @GetMapping("/ProductMasterApp")
    public String ProductMasterAppForm() {
        return "menu/ProductMasterApp";
    }

    @GetMapping("/ProductMasterKanri")
    public String ProductMasterKanriForm() {
        return "menu/ProductMasterKanri";
    }
    
    
    
    
    
    
}