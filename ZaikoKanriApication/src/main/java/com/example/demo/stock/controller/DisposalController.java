package com.example.demo.stock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.service.ProductService;
import com.example.demo.stock.entity.DisposalEntity;
import com.example.demo.stock.service.DisposalService;

@Controller
public class DisposalController {

    @Autowired
    private ProductService productService;

    @Autowired
    private DisposalService disposalService; // 追加

    // 商品一覧（廃棄登録画面）
    @GetMapping("/stock/DisposalRegister")
    public String DisposalRegister(
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) String janCode,
            Model model) {

        List<ProductEntity> allProducts = productService.findAll();
        List<ProductEntity> productList = allProducts;

        // JAN検索
        if (janCode != null && !janCode.isEmpty()) {
            productList = productList.stream()
                    .filter(p -> janCode.equals(p.getJanCode()))
                    .toList();
        }

        // 商品名検索
        if (productId != null) {
            productList = productList.stream()
                    .filter(p -> p.getId().equals(productId))
                    .toList();
        }

        if (productList.isEmpty()) {
            model.addAttribute("message", "一致する商品がありません");
        }

        model.addAttribute("productList", productList);
        model.addAttribute("allProducts", allProducts); // ★追加

        return "stock/DisposalRegister";
    }

    // 廃棄数入力画面
    @GetMapping("/stock/DisposalInput/{id}")
    public String disposalInput(@PathVariable Integer id, Model model) {
        ProductEntity product = productService.findById(id);
        if (product == null) {
            // 商品が存在しない場合は一覧に戻す
            return "redirect:/stock/DisposalRegister";
        }

        // DisposalEntity に productId をセットしておく
        DisposalEntity disposal = new DisposalEntity();
        disposal.setProductId(product.getId()); // ←ここがポイント

        model.addAttribute("product", product);
        model.addAttribute("disposal", disposal); // 新規廃棄データ
        return "stock/DisposalInput";
    }
    
    @PostMapping("/stock/DisposalInput")
    public String backToInput(@ModelAttribute DisposalEntity disposal, Model model) {

        ProductEntity product = productService.findById(disposal.getProductId());

        model.addAttribute("product", product);
        model.addAttribute("disposal", disposal);

        return "stock/DisposalInput";
    }
    
    @PostMapping("/stock/DisposalConfirm")
    public String disposalConfirm(
            @ModelAttribute DisposalEntity disposal,
            Model model) {

        // productId から商品情報を取得
        ProductEntity product = productService.findById(disposal.getProductId());

        // Model にセット
        model.addAttribute("product", product);
        model.addAttribute("disposal", disposal);

        return "stock/DisposalConfirm";
    }
    
    

    // 廃棄保存処理
    @PostMapping("/stock/DisposalSave")
    public String disposalSave(@ModelAttribute DisposalEntity disposal, Model model) {
        // 廃棄情報を保存
        disposalService.save(disposal);

        // 対象商品を取得
        ProductEntity product = productService.findById(disposal.getProductId());

        // 在庫を減らす
        int newStock = product.getStock() - disposal.getQuantity();
        product.setStock(newStock);
        productService.save(product); // ProductEntity の更新

        return "stock/DisposalComplete";
    }
}