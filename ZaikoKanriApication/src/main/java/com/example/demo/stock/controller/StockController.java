package com.example.demo.stock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ← 修正
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.service.ProductService;
import com.example.demo.stock.entity.StockInEntity;
import com.example.demo.stock.service.StockService;

@Controller
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;
    
    @Autowired
    private ProductService productService;

    @PostMapping("/StockInConfirm")
    public String confirm(@ModelAttribute StockInEntity stock, Model model) {

        ProductEntity product = productService.findById(stock.getProductId());

        model.addAttribute("stock", stock);
        model.addAttribute("product", product);

        return "stock/StockInConfirm";
    }

    // 在庫管理へ
    @GetMapping("/ZaikoKanri")
    public String ZaikoKanriForm() {
        return "stock/ZaikoKanri";
    }

    // 在庫確認へ
    @GetMapping("/ZaikoConfirm")
    public String ZaikoConfirmForm() {
        return "stock/ZaikoConfirm";
    }

    // 入荷処理
    @GetMapping("/ArrivalControll")
    public String arrivalForm(Model model) {

        model.addAttribute("stock", new StockInEntity());

        return "stock/ArrivalControll";
    }
    
    @GetMapping("/StockIn")
    public String stockInForm(Model model) {

        model.addAttribute("stock", new StockInEntity());

        List<ProductEntity> productList = productService.findAll();
        model.addAttribute("productList", productList);

        return "stock/StockIn";
    }
    
    @GetMapping("/StockInComplete")
    public String stockInCompleteForm() {
        return "stock/StockInComplete";
    }
    
    @PostMapping("/save")
    public String save(@ModelAttribute StockInEntity stock) {
        stockService.save(stock);
        return "/stock/StockInComplete";
    }
}