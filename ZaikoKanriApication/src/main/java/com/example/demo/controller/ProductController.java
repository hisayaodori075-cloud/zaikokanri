package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {

    // 商品新規登録画面
    @GetMapping("/product/newproduct")
    public String newProductForm() {
        return "product/newproduct"; // templates/product/newproduct.html に対応
    }

    // 将来的に POST で登録処理を追加する場合はここに書きます
    // @PostMapping("/product/newproduct")
    // public String createProduct(@RequestParam String name, ...) { ... }
}
