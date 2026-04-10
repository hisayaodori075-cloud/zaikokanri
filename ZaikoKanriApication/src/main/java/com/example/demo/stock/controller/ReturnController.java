package com.example.demo.stock.controller;

import java.time.LocalDate;
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

    // 商品一覧（返品登録画面）
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
            model.addAttribute("error", "一致する商品がありません");
        }

        model.addAttribute("productList", productList);
        model.addAttribute("allProducts", allProducts);

        return "stock/ReturnRegister";
    }

    // 返品数入力画面
    @GetMapping("/stock/ReturnInput/{id}")
    public String returnInput(@PathVariable Integer id, Model model) {
        ProductEntity product = productService.findById(id);
        if (product == null) {
            return "redirect:/stock/ReturnRegister";
        }

        ReturnEntity returnData = new ReturnEntity();
        returnData.setProductId(product.getId());

        model.addAttribute("product", product);
        model.addAttribute("returnData", returnData);
        return "stock/ReturnInput";
    }

    @PostMapping("/stock/ReturnInput")
    public String backToInput(@ModelAttribute ReturnEntity returnData, Model model) {
        ProductEntity product = productService.findById(returnData.getProductId());
        model.addAttribute("product", product);
        model.addAttribute("returnData", returnData);
        return "stock/ReturnInput";
    }

    @PostMapping("/stock/ReturnConfirm")
    public String returnConfirm(
            @ModelAttribute ReturnEntity returnData,
            Model model) {

        ProductEntity product = productService.findById(returnData.getProductId());

        if (product == null) {
            model.addAttribute("error", "商品が存在しません");
            return "stock/ReturnInput";
        }

        Integer stock = product.getStock();
        if (stock == null) stock = 0;

        Integer qty = returnData.getReturnQuantity();
        if (qty == null) qty = 0;

        // ★在庫チェック（追加）
        if (qty > stock) {
            model.addAttribute("error", "返品数が在庫数を超えています");
            model.addAttribute("product", product);
            model.addAttribute("returnData", returnData);
            return "stock/ReturnInput";
        }

        // ★数量チェック
        if (qty <= 0) {
            model.addAttribute("error", "返品数は1以上で入力してください");
            model.addAttribute("product", product);
            model.addAttribute("returnData", returnData);
            return "stock/ReturnInput";
        }

        // ★未来日チェック
        if (returnData.getReturnDate() != null &&
            returnData.getReturnDate().isAfter(LocalDate.now())) {

            model.addAttribute("error", "返品日は未来日を指定できません");
            model.addAttribute("product", product);
            model.addAttribute("returnData", returnData);
            return "stock/ReturnInput";
        }

        model.addAttribute("product", product);
        model.addAttribute("returnData", returnData);

        return "stock/ReturnConfirm";
    }

    // 返品保存処理
    @PostMapping("/stock/ReturnSave")
    public String returnSave(@ModelAttribute ReturnEntity returnData, Model model) {

        ProductEntity product = productService.findById(returnData.getProductId());

        if (product == null) {
            model.addAttribute("error", "商品が存在しません");
            return "stock/ReturnInput";
        }

        // ★最終防衛：在庫チェック
        Integer stock = product.getStock();
        if (stock == null) stock = 0;

        Integer qty = returnData.getReturnQuantity();
        if (qty == null) qty = 0;

        if (qty > stock) {
            model.addAttribute("error", "返品数が在庫数を超えています");
            model.addAttribute("product", product);
            model.addAttribute("returnData", returnData);
            return "stock/ReturnInput";
        }

        // ★数量チェック
        if (qty <= 0) {
            model.addAttribute("error", "返品数は1以上で入力してください");
            model.addAttribute("product", product);
            model.addAttribute("returnData", returnData);
            return "stock/ReturnInput";
        }

        // ★未来日チェック
        if (returnData.getReturnDate() != null &&
            returnData.getReturnDate().isAfter(java.time.LocalDate.now())) {

            model.addAttribute("error", "返品日は未来日を指定できません");
            model.addAttribute("product", product);
            model.addAttribute("returnData", returnData);
            return "stock/ReturnInput";
        }

        // ★Serviceに処理を委譲
        boolean result = returnService.executeReturn(returnData);

        if (!result) {
            model.addAttribute("error", "処理に失敗しました");
            return "stock/ReturnInput";
        }

        return "stock/ReturnComplete";
    }

    @GetMapping("/stock/ReturnMenu")
    public String returnMenuForm() {
        return "stock/ReturnMenu";
    }

    

    // 返品削除検索
    @GetMapping("/stock/ReturnDeleteSearch")
    public String showReturnDeleteSearch() {
        return "stock/ReturnDeleteSearch";
    }

    @PostMapping("/stock/ReturnDeleteConfirm")
    public String returnDeleteConfirm(@RequestParam("returnId") Integer id, Model model) {
        ReturnEntity returnData = returnService.findById(id);
        if (returnData == null) {
            model.addAttribute("error", "返品ID " + id + " は存在しません");
            return "stock/ReturnDeleteSearch";
        }

        ProductEntity product = productService.findById(returnData.getProductId());
        if (product == null) {
            model.addAttribute("error", "返品ID " + id + " の対象商品が存在しません");
            return "stock/ReturnDeleteSearch";
        }
        
        if (returnData.getCreatedAt() != null &&
        	    returnData.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusDays(7))) {

        	    model.addAttribute("error", "登録から7日経過したため削除できません");
        	    return "stock/ReturnDeleteSearch";
        	}

        model.addAttribute("returnData", returnData);
        model.addAttribute("product", product);
        return "stock/ReturnDeleteConfirm";
    }

    @PostMapping("/stock/ReturnDeleteComplete")
    public String returnDeleteComplete(@ModelAttribute ReturnEntity returnData, Model model) {

        ReturnEntity target = returnService.findById(returnData.getId());

        if (target == null) {
            model.addAttribute("error", "返品ID " + returnData.getId() + " が存在しません");
            return "stock/ReturnDeleteSearch";
        }
        
     // ★7日制限チェック（ここ追加）
        if (target.getCreatedAt() != null &&
            target.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusDays(7))) {

            model.addAttribute("error", "登録から7日経過したため削除できません");
            return "stock/ReturnDeleteSearch";
        }

        returnService.executeDelete(target.getId());

        return "stock/ReturnDeleteComplete";
    }

    @GetMapping("/stock/ReturnList")
    public String showReturnList(Model model) {
        List<ReturnEntity> returnList = returnService.findAllNotDeleted();
        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("returnList", returnList);
        model.addAttribute("productList", productList);

        return "stock/ReturnList";
    }
}