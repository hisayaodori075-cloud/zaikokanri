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
    
    @GetMapping("/StockInRegister")
    public String stockInRegister(
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

        return "stock/StockInRegister";
    }
    
    
    @GetMapping("/StockIn/{id}")
    public String stockInFromList(@PathVariable Integer id, Model model) {

        // ① 商品取得
        ProductEntity product = productService.findById(id);

        if (product == null) {
            return "redirect:/stock/StockInRegister";
        }

        // ② 既存画面用データ作成
        StockInEntity stock = new StockInEntity();
        stock.setProductId(product.getId()); // ←重要

        // ③ Modelに詰める
        model.addAttribute("product", product);
        model.addAttribute("stock", stock);

        // ★ここがポイント
        model.addAttribute("productList", List.of(product));

        // ④ 既存画面に遷移
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

	 // 入荷ID検索
	 

    // 入荷編集確認
    @PostMapping("/StockInEditConfirm")
    public String StockInConfirm(@ModelAttribute StockInEntity stock, Model model) {

        ProductEntity product = stockInService.findProductById(stock.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "この入荷データの対象商品は編集不可です");
            model.addAttribute("stock", stock);
            return "stock/StockInEditSearch";
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
    @PostMapping("/StockInDeleteConfirm")
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
    
    // 入荷ID検索
 
    @PostMapping("/StockInEditSearch")
    public String search(@RequestParam Integer id, Model model) {

        StockInEntity stock = stockInService.findById(id);

        if (stock == null) {
            model.addAttribute("errorMessage", "そのIDの入荷データは存在しません");
            model.addAttribute("stock", new StockInEntity());
            return "stock/StockInEditSearch";
        }

        // 商品取得（null 安全）
        ProductEntity stockProduct = null;
        if (stock.getProductId() != null) {
            stockProduct = productService.findById(stock.getProductId());
        }

        // 論理削除されていない商品リスト
        List<ProductEntity> productList = productService.findAll();
        productList.removeIf(ProductEntity::isDeleted);

        // 編集中の商品をリストに確実に追加
        if (stockProduct != null && !productList.contains(stockProduct)) {
            productList.add(stockProduct);
        }

        model.addAttribute("stock", stock);
        model.addAttribute("productList", productList);

        return "stock/StockInEdit";
    }

    // 入荷編集画面（GET）
    @GetMapping("/StockInEdit/{id}")
    public String edit(@PathVariable Integer id, Model model) {

        StockInEntity stock = stockInService.findById(id);

        if (stock == null) {
            model.addAttribute("errorMessage", "そのIDの入荷データは存在しません");
            model.addAttribute("stock", new StockInEntity());
            return "stock/StockInEditSearch";
        }

        ProductEntity stockProduct = null;
        if (stock.getProductId() != null) {
            stockProduct = productService.findById(stock.getProductId());
        }

        List<ProductEntity> productList = productService.findAll();
        productList.removeIf(ProductEntity::isDeleted);

        if (stockProduct != null && !productList.contains(stockProduct)) {
            productList.add(stockProduct);
        }

        model.addAttribute("stock", stock);
        model.addAttribute("productList", productList);

        return "stock/StockInEdit";
    }

    // 入荷編集POST（再表示用）
    @PostMapping("/StockInEdit")
    public String stockInEdit(@RequestParam Integer id, Model model) {

        if (id == null) { // ← null チェック追加
            model.addAttribute("errorMessage", "IDが指定されていません");
            model.addAttribute("stock", new StockInEntity());
            return "stock/StockInEditSearch";
        }

        StockInEntity stock = stockInService.findById(id);

        if (stock == null) {
            model.addAttribute("errorMessage", "そのIDの入荷データは存在しません");
            model.addAttribute("stock", new StockInEntity());
            return "stock/StockInEditSearch";
        }

        ProductEntity stockProduct = null;
        if (stock.getProductId() != null) {
            stockProduct = productService.findById(stock.getProductId());
        }

        List<ProductEntity> productList = productService.findAll();
        productList.removeIf(ProductEntity::isDeleted);

        if (stockProduct != null && !productList.contains(stockProduct)) {
            productList.add(stockProduct);
        }

        model.addAttribute("stock", stock);
        model.addAttribute("productList", productList);

        return "stock/StockInEdit";
    }
    
    @PostMapping("/save")
    public String save(@ModelAttribute StockInEntity stock) {
        stockInService.update(stock);
        return "/stock/StockInEditComplete";
    }
    
    @PostMapping("/StockInSave")
    public String stockInSave(@ModelAttribute StockInEntity stock) {
        stockInService.save(stock);
        return "/stock/StockInComplete";
    }
    
    @GetMapping("/StockList")
    public String stockList(
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) String janCode,
            @RequestParam(required = false) String productName,
            Model model) {

        List<ProductEntity> productList;

        boolean hasJan = janCode != null && !janCode.isEmpty();
        boolean hasProduct = productId != null;

        // JAN + 商品名（プルダウン）チェック
        if (hasJan && hasProduct) {

            ProductEntity product = productService.findById(productId);

            if (product != null && product.getJanCode().equals(janCode)) {
                productList = List.of(product);
            } else {
                productList = List.of();
                model.addAttribute("errorMessage", "JANコードと商品名が一致する商品はありません");
            }

        }
        // JANのみ
        else if (hasJan) {

            ProductEntity product = productService.findByJanCode(janCode);

            if (product == null) {
                productList = List.of();
                model.addAttribute("errorMessage", "そのJANコードの商品は存在しません");
            } else {
                productList = List.of(product);
            }

        }
        // 商品名のみ（プルダウン）
        else if (hasProduct) {

            ProductEntity product = productService.findById(productId);
            productList = (product == null) ? List.of() : List.of(product);

        }
        // 未入力
        else {

            productList = productService.findAll();

        }

        model.addAttribute("productList", productList);
        model.addAttribute("allProducts", productService.findAll());

        return "stock/StockList";
    }
    
 // ---------------- 入荷履歴一覧 ----------------
    @GetMapping("/StockInList")
    public String stockInList(Model model) {

        List<StockInEntity> stockInList = stockInService.getStockInList();
        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("stockInList", stockInList);
        model.addAttribute("productList", productList);

        return "stock/StockInList";
    }
}
