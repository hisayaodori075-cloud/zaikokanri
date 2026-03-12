package com.example.demo.stock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ← 修正
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.service.ProductService;
import com.example.demo.stock.entity.StockInEntity;
import com.example.demo.stock.service.StockInService;

@Controller
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockInService stockInService;
    
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
    
    // 入荷処理完了
    @GetMapping("/StockInComplete")
    public String stockInCompleteForm() {
        return "stock/StockInComplete";
    }
       
    // 入荷ID検索画面
    @GetMapping("/StockInEditSearch")
    public String stockInEditSearchForm(Model model) {  
    	model.addAttribute("stock", new StockInEntity());
        return "stock/StockInEditSearch";
    }
    
    @PostMapping("/StockInEditSearch")
    public String search(@RequestParam Integer id, Model model) {

    	StockInEntity stock = stockInService.findById(id);

        if (stock == null) {
            model.addAttribute("errorMessage", "そのIDの入荷データは存在しません");
            model.addAttribute("stock", new StockInEntity()); // ← これ重要
            return "stock/StockInEditSearch";
        }

        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("stock", stock);
        model.addAttribute("productList", productList);

        return "stock/StockInEdit";
    }
    
    // 入荷処理編集画面
    @GetMapping("/StockInEdit/{id}")
    public String edit(@PathVariable Integer id, Model model){

        StockInEntity stock = stockInService.findById(id);

        if (stock == null) {
            model.addAttribute("errorMessage", "そのIDの入荷データは存在しません");
            model.addAttribute("stock", new StockInEntity());
            return "stock/StockInEditSearch";
        }

        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("stock", stock);
        model.addAttribute("productList", productList);

        return "stock/StockInEdit";
    }
    
    @PostMapping("/StockInEdit")
    public String stockInEdit(@RequestParam Integer id, Model model) {

        StockInEntity stock = stockInService.findById(id);

        // ★ここ追加
        if (stock == null) {
            model.addAttribute("errorMessage", "そのIDの入荷データは存在しません");
            model.addAttribute("stock", new StockInEntity());
            return "stock/StockInEditSearch";
        }

        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("stock", stock);
        model.addAttribute("productList", productList);

        return "stock/StockInEdit";
    }
    

    @PostMapping("/StockInEditConfirm")
    public String StockInConfirm(@ModelAttribute StockInEntity stock, Model model) {

        ProductEntity product = productService.findById(stock.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "その商品IDは存在しません");
            model.addAttribute("stock", stock);
            return "stock/StockIn";
        }

        model.addAttribute("stock", stock);
        model.addAttribute("product", product);

        return "stock/StockInEditConfirm";
    }
    
    @GetMapping("/StockEditInComplete")
    public String stockEditInCompleteForm() {
        return "stock/StockEditInComplete";
    }
    
    // 入荷削除検索画面
    @GetMapping("/StockInDeleteSearch")
    public String stockInDeleteSearchForm(Model model) {
        model.addAttribute("stock", new StockInEntity());
        return "stock/StockInDeleteSearch";
    }

    // 入荷削除確認
    @PostMapping("/StockInDelete")
    public String deleteConfirm(@RequestParam Integer id, Model model) {
        StockInEntity stock = stockInService.findById(id);

        if (stock == null || stock.isDeleted()) {  // ← 論理削除済みも存在しない扱い
            model.addAttribute("errorMessage", "そのIDの入荷データは存在しません");
            return "stock/StockInDeleteSearch";
        }

        ProductEntity product = productService.findById(stock.getProductId());

        model.addAttribute("stock", stock);
        model.addAttribute("product", product);

        return "stock/StockInDeleteConfirm";
    }

    // 入荷削除完了（論理削除）
    @PostMapping("/StockInDeleteComplete/{id}")
    public String deleteComplete(@PathVariable Integer id) {
        stockInService.delete(id); // ← Service 側で論理削除処理
        return "stock/StockInDeleteComplete";
    }
    
    @PostMapping("/save")
    public String save(@ModelAttribute StockInEntity stock) {
        stockInService.save(stock);
        return "/stock/StockInEditComplete";
    }
    
    @GetMapping("/StockSearch")
    public String stockSearch(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String janCode,
            Model model) {

        List<ProductEntity> productList;

        if ((productName == null || productName.isEmpty()) &&
            (janCode == null || janCode.isEmpty())) {

            productList = productService.findAll();

        } else if (productName != null && !productName.isEmpty()) {

            productList = productService.findByProductNameContaining(productName);

        } else {

            ProductEntity product = productService.findByJanCode(janCode);

            if (product == null) {
                productList = List.of();
            } else {
                productList = List.of(product);
            }
        }

        model.addAttribute("productList", productList);

        return "stock/StockSearch";
    }
    
    @GetMapping("/StockList")
    public String stockList(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String janCode,
            Model model){

        List<ProductEntity> productList;

        if ((productName == null || productName.isEmpty()) &&
            (janCode == null || janCode.isEmpty())) {

            productList = productService.findAll();

        } else if (productName != null && !productName.isEmpty()) {

            productList = productService.findByProductNameContaining(productName);

        } else {

            ProductEntity product = productService.findByJanCode(janCode);

            if (product == null) {
                productList = List.of();
            } else {
                productList = List.of(product);
            }
        }

        model.addAttribute("productList", productList);

        return "stock/StockList";
    }
}