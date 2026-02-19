package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Product;

@Controller
@RequestMapping("/product")
public class ProductController {

    // 新規登録画面表示
    @GetMapping("/newproduct")
    public String showForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/newproduct";
    }

    // 確認画面へ
    @PostMapping("/confirm")
    public String confirm(@ModelAttribute Product product, Model model) {
        model.addAttribute("product", product);
        return "product/confirm";
    }

    // 登録処理
    @PostMapping("/save")
    public String save(@ModelAttribute Product product, Model model) {

        // ★ ここでDB保存処理を書く（今は仮でOK）
        System.out.println("保存：" + product.getName());

        model.addAttribute("product", product);
        return "product/complete";
    }
}