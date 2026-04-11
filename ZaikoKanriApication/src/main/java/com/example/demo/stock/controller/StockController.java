package com.example.demo.stock.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    
    // 戻る用（予定）
    @PostMapping("/StockIn")
    public String backToInput(@ModelAttribute StockInEntity stock, Model model) {

        ProductEntity product = productService.findById(stock.getProductId());

        model.addAttribute("product", product);
        model.addAttribute("stock", stock);

        return "stock/StockIn";
    }
    
    
    @PostMapping("/StockInConfirm")
    public String confirm(@ModelAttribute StockInEntity stock, Model model) {

        LocalDate today = LocalDate.now();

        ProductEntity product = productService.findById(stock.getProductId());

        // ===============================
        // バリデーション①：商品存在チェック
        // ===============================
        if (product == null) {
            model.addAttribute("errorMessage", "商品が存在しません");
            return "stock/StockIn";
        }

        // ===============================
        // バリデーション②：数量チェック（重要追加）
        // ===============================
        if (stock.getQuantity() == null || stock.getQuantity() <= 0) {
            model.addAttribute("errorMessage", "入荷数は1以上で入力してください");

            model.addAttribute("product", product);
            model.addAttribute("stock", stock);

            return "stock/StockIn";
        }

        // ===============================
        // バリデーション③：未来日チェック
        // ===============================
        if (stock.getArrivalDate() != null && stock.getArrivalDate().isAfter(today)) {
            model.addAttribute("errorMessage", "入荷日は未来日を指定できません");

            model.addAttribute("product", product);
            model.addAttribute("stock", stock);

            return "stock/StockIn";
        }

        // ===============================
        // 正常系
        // ===============================
        model.addAttribute("stock", stock);
        model.addAttribute("product", product);

        return "stock/StockInConfirm";
    }
    
    // 入荷処理完了
    @PostMapping("/StockInSave")
    public String stockInSave(@ModelAttribute StockInEntity stock, Model model) {

        LocalDate today = LocalDate.now();

        ProductEntity product =
                productService.findById(stock.getProductId());

        // ===============================
        // 商品存在チェック
        // ===============================
        if (product == null) {
            model.addAttribute("errorMessage", "商品が存在しません");
            return "stock/StockIn";
        }

        // ===============================
        // 数量チェック
        // ===============================
        if (stock.getQuantity() == null || stock.getQuantity() <= 0) {
            model.addAttribute("errorMessage", "入荷数は1以上で入力してください");

            model.addAttribute("product", product);
            model.addAttribute("stock", stock);

            return "stock/StockIn";
        }

        // ===============================
        // 未来日チェック
        // ===============================
        if (stock.getArrivalDate() != null && stock.getArrivalDate().isAfter(today)) {
            model.addAttribute("errorMessage", "入荷日は未来日を指定できません");

            model.addAttribute("product", product);
            model.addAttribute("stock", stock);

            return "stock/StockIn";
        }

        // ===============================
        // 正常処理
        // ===============================
        stockInService.executeArrival(stock);

        return "/stock/StockInComplete";
    }
    
    // 入荷ID検索画面
    @GetMapping("/StockInEditSearch")
    public String stockInEditSearchForm(Model model) {
        model.addAttribute("stock", new StockInEntity());
        return "stock/StockInEditSearch";
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
        
        if (stock.getCreatedAt() != null &&
        	    stock.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {

        	    model.addAttribute("errorMessage", "登録から7日経過しているため編集できません");
        	    model.addAttribute("stock", new StockInEntity());

        	    return "stock/StockInEditSearch";
        	}

        ProductEntity product = productService.findById(stock.getProductId());

        model.addAttribute("stock", stock);
        model.addAttribute("product", product); // ←これ追加

        return "stock/StockInEdit";
    }
    
    @PostMapping("/StockInEditConfirm")
    public String StockInConfirm(@ModelAttribute StockInEntity stock, Model model) {

        ProductEntity product =
                stockInService.findProductById(stock.getProductId());

        // ===============================
        // 数量チェック
        // ===============================
        if (stock.getQuantity() == null || stock.getQuantity() <= 0) {
            model.addAttribute("errorMessage", "入荷数は1以上で入力してください");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);

            return "stock/StockInEdit";
        }

        // ===============================
        // 未来日チェック
        // ===============================
        LocalDate today = LocalDate.now();

        if (stock.getArrivalDate() != null &&
            stock.getArrivalDate().isAfter(today)) {

            model.addAttribute("errorMessage", "入荷日は未来日を指定できません");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);

            return "stock/StockInEdit";
        }

        // ===============================
        // ★在庫チェック（Serviceへ移動）
        // ===============================
        boolean result = stockInService.canEdit(stock);

        if (!result) {
            model.addAttribute("errorMessage", "在庫が不足するためこの変更はできません");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);

            return "stock/StockInEdit";
        }

        // ===============================
        // 正常系
        // ===============================
        model.addAttribute("stock", stock);
        model.addAttribute("product", product);

        return "stock/StockInEditConfirm";
    }
    
 // 戻る用（廃棄と統一）
    @GetMapping("/StockInEdit/{id}")
    public String edit(@PathVariable Integer id, Model model) {

        StockInEntity stock = stockInService.findById(id);

        if (stock == null) {
            model.addAttribute("errorMessage", "入荷ID " + id + " は存在しません");
            return "stock/StockInEditSearch";
        }

        // 商品取得
        ProductEntity product = productService.findById(stock.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "入荷ID " + id + " の対象商品が存在しません");
            return "stock/StockInEditSearch";
        }

        // 必要なら（select使うなら）
        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("stock", stock);
        model.addAttribute("product", product);
        model.addAttribute("productList", productList);

        return "stock/StockInEdit";
    }
    
    @PostMapping("/save")
    public String save(@ModelAttribute StockInEntity stock, Model model) {

        ProductEntity product =
                productService.findById(stock.getProductId());

        // ===============================
        // 商品存在チェック
        // ===============================
        if (product == null) {
            model.addAttribute("errorMessage", "商品が存在しません");
            model.addAttribute("stock", stock);
            return "stock/StockInEditSearch";
        }

        // ===============================
        // ★7日制限（編集最終チェック）
        // ===============================
        LocalDateTime limit = LocalDateTime.now().minusDays(7);

        if (stock.getCreatedAt() != null &&
            stock.getCreatedAt().isBefore(limit)) {

            model.addAttribute("errorMessage", "登録から7日経過しているため編集できません");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);

            return "stock/StockInEditSearch";
        }

        // ===============================
        // 数量チェック
        // ===============================
        if (stock.getQuantity() == null || stock.getQuantity() <= 0) {
            model.addAttribute("errorMessage", "入荷数は1以上で入力してください");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);

            return "stock/StockInEdit";
        }

        // ===============================
        // 未来日チェック
        // ===============================
        LocalDate today = LocalDate.now();

        if (stock.getArrivalDate() != null &&
            stock.getArrivalDate().isAfter(today)) {

            model.addAttribute("errorMessage", "入荷日は未来日を指定できません");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);

            return "stock/StockInEdit";
        }

        // ===============================
        // 正常処理
        // ===============================
        stockInService.update(stock);

        return "/stock/StockInEditComplete";
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

        // ===============================
        // 存在チェック
        // ===============================
        if (stock == null) {
            model.addAttribute("errorMessage", "入荷ID " + id + " は存在しません");
            return "stock/StockInDeleteSearch";
        }

        // ===============================
        // 商品取得
        // ===============================
        ProductEntity product = productService.findById(stock.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "入荷ID " + id + " の対象商品が存在しません");
            return "stock/StockInDeleteSearch";
        }

        // ===============================
        // ★7日制限
        // ===============================
        if (stock.getCreatedAt() != null &&
            stock.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {

            model.addAttribute("errorMessage", "登録から7日経過したため削除できません");
            return "stock/StockInDeleteSearch";
        }
        
        // ===============================
        // 在庫不足チェック（ここが追加）
        // ===============================
        int currentStock = product.getStock() == null ? 0 : product.getStock();
        int deleteQty = stock.getQuantity() == null ? 0 : stock.getQuantity();

        int newStock = currentStock - deleteQty;

        if (newStock < 0) {
            model.addAttribute("errorMessage", "在庫不足のため削除できません（在庫が不足します）");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);

            return "stock/StockInDeleteSearch";
        }

        // ===============================
        // 正常系
        // ===============================
        model.addAttribute("stock", stock);
        model.addAttribute("product", product);

        return "stock/StockInDeleteConfirm";
    }
        

    @PostMapping("/StockInDeleteComplete")
    public String deleteComplete(@RequestParam Integer id, Model model) {

        StockInEntity stock = stockInService.findById(id);

        // ===============================
        // 存在チェック
        // ===============================
        if (stock == null) {
            model.addAttribute("errorMessage", "入荷ID " + id + " は存在しません");
            return "stock/StockInDeleteSearch";
        }

        // ===============================
        // ★7日制限（廃棄と統一）
        // ===============================
        if (stock.getCreatedAt() != null &&
            stock.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {

            model.addAttribute("errorMessage", "登録から7日経過したため削除できません");
            return "stock/StockInDeleteSearch";
        }

        // ===============================
        // 削除実行
        // ===============================
        stockInService.executeDelete(id);
        
        // ===============================
        // ★在庫不足エラー
        // ===============================
        boolean result = stockInService.executeDelete(id);
        
        if (!result) {
            model.addAttribute("errorMessage", "在庫不足のため削除できません（在庫がマイナスになります）");
            model.addAttribute("stock", stock);
            return "stock/StockInDeleteSearch";
        }

        return "stock/StockInDeleteComplete";
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
