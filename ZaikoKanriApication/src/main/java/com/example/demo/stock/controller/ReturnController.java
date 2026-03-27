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
import com.example.demo.stock.entity.ReturnEntity;
import com.example.demo.stock.service.ReturnService;

@Controller
public class ReturnController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReturnService returnService;

    // ===============================
    // 返品メニュー
    // ===============================
    @GetMapping("/stock/ReturnMenu")
    public String returnMenu() {
        return "stock/ReturnMenu";
    }

    // ===============================
    // 商品一覧（返品登録画面）
    // ===============================
    @GetMapping("/stock/ReturnRegister")
    public String returnRegister(
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
        model.addAttribute("allProducts", allProducts);

        return "stock/ReturnRegister";
    }

    // ===============================
    // 返品入力画面
    // ===============================
    @GetMapping("/stock/ReturnInput/{id}")
    public String returnInput(@PathVariable Integer id, Model model) {

        ProductEntity product = productService.findById(id);

        if (product == null) {
            return "redirect:/stock/ReturnRegister";
        }

        ReturnEntity returnData = new ReturnEntity();
        returnData.setProductId(product.getId());

        model.addAttribute("product", product);
        model.addAttribute("return", returnData);

        return "stock/ReturnInput";
    }

    // 戻る用
    @PostMapping("/stock/ReturnInput")
    public String backToInput(@ModelAttribute ReturnEntity returnData, Model model) {

        ProductEntity product = productService.findById(returnData.getProductId());

        model.addAttribute("product", product);
        model.addAttribute("return", returnData);

        return "stock/ReturnInput";
    }

    // ===============================
    // 確認画面
    // ===============================
    @PostMapping("/stock/ReturnConfirm")
    public String returnConfirm(@ModelAttribute ReturnEntity returnData, Model model) {

        ProductEntity product = productService.findById(returnData.getProductId());

        model.addAttribute("product", product);
        model.addAttribute("return", returnData);

        return "stock/ReturnConfirm";
    }

    // ===============================
    // 保存処理
    // ===============================
    @PostMapping("/stock/ReturnSave")
    public String returnSave(@ModelAttribute ReturnEntity returnData) {

        // 保存
        returnService.save(returnData);

        // 在庫更新（返品＝マイナス）
        ProductEntity product = productService.findById(returnData.getProductId());

        int newStock = product.getStock() - returnData.getReturnQuantity();
        product.setStock(newStock);

        productService.save(product);

        return "stock/ReturnComplete";
    }

    // ===============================
    // 返品一覧
    // ===============================
    @GetMapping("/stock/ReturnList")
    public String showReturnList(Model model) {

        List<ReturnEntity> returnList = returnService.findAll();

        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("returnList", returnList);
        model.addAttribute("productList", productList);

        return "stock/ReturnList";
    }

    // ===============================
    // 返品削除（検索画面）
    // ===============================
    @GetMapping("/stock/ReturnDeleteSearch")
    public String showReturnDeleteSearch() {
        return "stock/ReturnDeleteSearch";
    }

    // ===============================
    // 削除確認
    // ===============================
    @PostMapping("/stock/ReturnDeleteConfirm")
    public String returnDeleteConfirm(@RequestParam("returnId") Integer id, Model model) {

        ReturnEntity returnData = returnService.findById(id);

        if (returnData == null) {
            model.addAttribute("errorMessage", "返品ID " + id + " は存在しません");
            return "stock/ReturnDeleteSearch";
        }

        ProductEntity product = productService.findById(returnData.getProductId());

        model.addAttribute("return", returnData);
        model.addAttribute("product", product);

        return "stock/ReturnDeleteConfirm";
    }

    // ===============================
    // 削除実行
    // ===============================
    @PostMapping("/stock/ReturnDeleteComplete")
    public String returnDeleteComplete(@ModelAttribute ReturnEntity returnData, Model model) {

        ReturnEntity target = returnService.findById(returnData.getId());

        if (target == null) {
            model.addAttribute("errorMessage", "返品IDが存在しません");
            return "stock/ReturnDeleteSearch";
        }

        ProductEntity product = productService.findById(target.getProductId());

        // 在庫戻す
        int restoredStock = product.getStock() + target.getReturnQuantity();
        product.setStock(restoredStock);

        productService.save(product);

        // 削除
        returnService.delete(target);

        return "stock/ReturnDeleteComplete";
    }
}