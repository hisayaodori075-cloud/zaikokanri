package com.example.demo.menu;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.product.ProductEntity;

@RequestMapping("/menu")
@Controller
public class MenuController {

    @Autowired
    private MenuProductRepository productRepository;

    // ログイン後画面
    @GetMapping("/ProductMasterApp")
    public String ProductMasterAppForm() {
        return "menu/ProductMasterApp";
    }

    @GetMapping("/ProductMasterKanri")
    public String ProductMasterKanriForm() {
        return "menu/ProductMasterKanri";
    }

    // ✅ 商品マスタ一覧表示（DBから取得）
    @GetMapping("/ProductMasterList")
    public String ProductMasterListForm(Model model) {

        List<ProductEntity> productList = productRepository.findAll();

        model.addAttribute("productList", productList);

        return "menu/ProductMasterList";
    }
    
    @GetMapping("/ZaikoKanri")
    public String ZaikoKanriForm() {
        return "menu/ZaikoKanri";
    }
    
    @GetMapping("/ZaikoConfirm")
    public String ZaikoConfirmForm() {
        return "menu/ZaikoConfirm";
    }
    
    
}