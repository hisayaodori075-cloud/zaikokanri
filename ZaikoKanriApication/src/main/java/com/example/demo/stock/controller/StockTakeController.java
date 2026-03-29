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
import com.example.demo.stock.entity.StockTakeEntity;
import com.example.demo.stock.service.StockTakeService;

@Controller
public class StockTakeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private StockTakeService stockTakeService;

    // 商品一覧（棚卸登録画面）
    @GetMapping("/stock/StockTakeRegister")
    public String stockTakeRegister(
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

        return "stock/StockTakeRegister";
    }

    // 棚卸実在庫入力画面
    @GetMapping("/stock/StockTake/{id}")
    public String stockTakeInput(@PathVariable Integer id, Model model) {

        ProductEntity product = productService.findById(id);
        if (product == null) {
            return "redirect:/stock/StockTakeRegister";
        }

        StockTakeEntity stockTake = new StockTakeEntity();
        stockTake.setProductId(product.getId());

        model.addAttribute("product", product);
        model.addAttribute("stockTake", stockTake);

        return "stock/StockTake";
    }

    // 確認画面
    @PostMapping("/stock/StockTakeConfirm")
    public String stockTakeConfirm(
            @ModelAttribute StockTakeEntity stockTake,
            Model model) {

        ProductEntity product = productService.findById(stockTake.getProductId());

        int systemStock = product.getStock();
        int diff = stockTake.getRealStock() - systemStock;

        model.addAttribute("product", product);
        model.addAttribute("stockTake", stockTake);
        model.addAttribute("systemStock", systemStock);
        model.addAttribute("diff", diff);

        return "stock/StockTakeConfirm";
    }

    // 保存処理
    @PostMapping("/stock/StockTakeSave")
    public String stockTakeSave(
            @ModelAttribute StockTakeEntity stockTake,
            Model model) {

        ProductEntity product = productService.findById(stockTake.getProductId());

        // 在庫を実在庫に合わせる
        product.setStock(stockTake.getRealStock());
        productService.save(product);

        // 履歴保存
        stockTakeService.save(stockTake);

        return "stock/StockTakeComplete";
    }

    // 編集画面（必要なら）
    @GetMapping("/stock/StockTakeEdit/{id}")
    public String stockTakeEdit(@PathVariable Integer id, Model model) {

        StockTakeEntity stockTake = stockTakeService.findById(id);

        if (stockTake == null) {
            model.addAttribute("errorMessage", "棚卸ID " + id + " は存在しません");
            return "stock/StockTakeEditSearch";
        }

        ProductEntity product = productService.findById(stockTake.getProductId());

        model.addAttribute("stockTake", stockTake);
        model.addAttribute("product", product);

        return "stock/StockTakeEdit";
    }

    // 編集確認
    @PostMapping("/stock/StockTakeEditConfirm")
    public String stockTakeEditConfirm(
            @ModelAttribute StockTakeEntity stockTake,
            Model model) {

        ProductEntity product = productService.findById(stockTake.getProductId());

        int systemStock = product.getStock();
        int diff = stockTake.getRealStock() - systemStock;

        model.addAttribute("stockTake", stockTake);
        model.addAttribute("product", product);
        model.addAttribute("systemStock", systemStock);
        model.addAttribute("diff", diff);

        return "stock/StockTakeEditConfirm";
    }

    // 編集保存
    @PostMapping("/stock/StockTakeEditSave")
    public String stockTakeEditSave(
            @ModelAttribute StockTakeEntity stockTake,
            Model model) {

        StockTakeEntity old = stockTakeService.findById(stockTake.getId());

        if (old == null) {
            model.addAttribute("errorMessage", "棚卸IDが存在しません");
            return "stock/StockTakeEditConfirm";
        }

        ProductEntity product = productService.findById(stockTake.getProductId());

        // 差分反映（更新なので注意）
        product.setStock(stockTake.getRealStock());
        productService.save(product);

        stockTakeService.save(stockTake);

        return "stock/StockTakeEditComplete";
    }

    // 削除（論理削除）
    @GetMapping("/stock/StockTakeDelete/{id}")
    public String stockTakeDelete(@PathVariable Integer id, Model model) {

        StockTakeEntity stockTake = stockTakeService.findById(id);

        if (stockTake == null) {
            model.addAttribute("errorMessage", "棚卸IDが存在しません");
            return "stock/StockTakeDeleteSearch";
        }

        ProductEntity product = productService.findById(stockTake.getProductId());

        model.addAttribute("stockTake", stockTake);
        model.addAttribute("product", product);

        return "stock/StockTakeDeleteConfirm";
    }

    // 削除確定
    @PostMapping("/stock/StockTakeDeleteComplete")
    public String stockTakeDeleteComplete(
            @ModelAttribute StockTakeEntity stockTake,
            Model model) {

        StockTakeEntity target = stockTakeService.findById(stockTake.getId());

        if (target == null) {
            model.addAttribute("errorMessage", "棚卸IDが存在しません");
            return "stock/StockTakeDeleteSearch";
        }

        target.setDeleted(true);
        stockTakeService.save(target);

        return "stock/StockTakeDeleteComplete";
    }

    // 一覧
    @GetMapping("/stock/StockTakeList")
    public String stockTakeList(Model model) {

        List<StockTakeEntity> stockTakeList =
                stockTakeService.findAllNotDeleted();

        List<ProductEntity> productList =
                productService.findAll();

        model.addAttribute("stockTakeList", stockTakeList);
        model.addAttribute("productList", productList);

        return "stock/StockTakeList";
    }

    // メニュー
    @GetMapping("/stock/StockTakeMenu")
    public String stockTakeMenu() {
        return "stock/StockTakeMenu";
    }
}