package com.example.demo.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.repository.ProductRepository;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // 新規登録画面表示
    @GetMapping("/newproduct")
    public String showForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        return "product/newproduct";
    }
    

    // 確認画面へ
    @PostMapping("/confirm")
    public String confirm(@ModelAttribute ProductEntity product, Model model) {
        model.addAttribute("product", product);
        return "product/confirm";
    }
    
    
    
 // ✅ 商品マスタ一覧表示（DBから取得）
    @GetMapping("/ProductMasterList")
    public String ProductMasterListForm(Model model) {

        List<ProductEntity> productList = productRepository.findAll();

        model.addAttribute("productList", productList);

        return "product/ProductMasterList";
    }
    

    // 登録処理
    @PostMapping("/save")
    public String save(@ModelAttribute ProductEntity product, Model model) {

        // 🔽 Model → Entity に変換
        ProductEntity entity = new ProductEntity();
        entity.setJanCode(product.getJanCode());
        entity.setMakerName(product.getMakerName());
        entity.setProductName(product.getProductName());
        entity.setPurchasePrice(product.getPurchasePrice());
        entity.setPrice(product.getPrice());
        entity.setStatus(product.getStatus());
        entity.setSalesStatus(product.getSalesStatus());

        // 🔽 DB保存
        productRepository.save(entity);

        model.addAttribute("product", product);
        return "product/complete";
    }
}