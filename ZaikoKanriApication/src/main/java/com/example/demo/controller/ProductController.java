package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.dto.ProductDto;

@Controller
public class ProductController {

    // 画面表示
    @GetMapping("/product")
    public String showForm(Model model) {
        model.addAttribute("productDto", new ProductDto());
        return "product-form";
    }

    // 登録処理（まだ保存はしない）
    @PostMapping("/product")
    public String register(ProductDto productDto, Model model) {

        model.addAttribute("name", productDto.getName());
        model.addAttribute("price", productDto.getPrice());

        return "product-result";
    }
}
