package com.example.demo.stock.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpSession;

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
    
    // ---------------- 入荷数入力 ----------------
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
            model.addAttribute("errorMessage", "一致する商品がありません");
        }

        model.addAttribute("productList", productList);
        model.addAttribute("allProducts", allProducts);

        return "stock/StockInRegister";
    }
    
    // 入荷数入力画面
    @GetMapping("/StockIn/{id}")
    public String stockInInput(@PathVariable Integer id, Model model) {

        // ★削除されていない商品だけ取得
        ProductEntity product = productService.findByIdAndDeletedFalse(id);

        // ★存在しない or 論理削除済は弾く
        if (product == null) {
            return "redirect:/stock/StockInRegister";
        }

        // StockInEntity に productId をセットしておく
        StockInEntity stockIn = new StockInEntity();
        stockIn.setProductId(product.getId());

        model.addAttribute("product", product);
        model.addAttribute("stock", stockIn);

        return "stock/StockIn";
    }
    
    // 戻る用（予定）
    @PostMapping("/StockIn")
    public String backToInput(@ModelAttribute StockInEntity stock, Model model) {

        // ★① 商品取得（論理削除込み）
        ProductEntity product =
                productService.findByIdAndDeletedFalse(stock.getProductId());

        // ★② 存在チェック
        if (product == null) {
            return "redirect:/stock/StockInRegister";
        }

        model.addAttribute("product", product);
        model.addAttribute("stock", stock);

        return "stock/StockIn";
    }
    
    
    @GetMapping("/StockInConfirm")
    public String stockInConfirmGet() {
        return "redirect:/stock/StockInRegister";
    }
    
    @PostMapping("/StockInConfirm")
    public String confirm(@ModelAttribute StockInEntity stock, Model model, HttpSession session) {

        LocalDate today = LocalDate.now();

        // ★① 商品取得（論理削除込みに統一）
        ProductEntity product =
                productService.findByIdAndDeletedFalse(stock.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "商品が存在しません");
            return "stock/StockIn";
        }

        // ★② 数量チェック（先にやる）
        if (stock.getQuantity() == null || stock.getQuantity() <= 0) {
            model.addAttribute("errorMessage", "入荷数は1以上で入力してください");

            model.addAttribute("product", product);
            model.addAttribute("stock", stock);

            return "stock/StockIn";
        }

        // ★③ 未来日チェック
        if (stock.getArrivalDate() != null && stock.getArrivalDate().isAfter(today)) {
            model.addAttribute("errorMessage", "入荷日は未来日を指定できません");

            model.addAttribute("product", product);
            model.addAttribute("stock", stock);

            return "stock/StockIn";
        }

        // ★④ 正常系
        model.addAttribute("stock", stock);
        model.addAttribute("product", product);

        // ★⑤ 確認フラグ（Disposalと統一するなら追加推奨）
        session.setAttribute("stockInConfirm", true);

        return "stock/StockInConfirm";
    }
    
    @GetMapping("/StockInSave")
    public String stockInSaveGet() {
        return "redirect:/stock/StockInRegister";
    }
    
    // 入荷処理完了
    @PostMapping("/StockInSave")
    public String stockInSave(@ModelAttribute StockInEntity stock,
                              Model model,
                              HttpSession session) {

        Boolean flag = (Boolean) session.getAttribute("stockInConfirm");

        if (flag == null || !flag) {
            return "redirect:/stock/StockInRegister";
        }

        session.removeAttribute("stockInConfirm");

        LocalDate today = LocalDate.now();

        ProductEntity product =
                productService.findByIdAndDeletedFalse(stock.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "商品が存在しません");
            return "stock/StockIn";
        }

        if (stock.getQuantity() == null || stock.getQuantity() <= 0) {
            model.addAttribute("errorMessage", "入荷数は1以上で入力してください");
            model.addAttribute("product", product);
            model.addAttribute("stock", stock);
            return "stock/StockIn";
        }

        if (stock.getArrivalDate() != null &&
            stock.getArrivalDate().isAfter(today)) {

            model.addAttribute("errorMessage", "入荷日は未来日を指定できません");
            model.addAttribute("product", product);
            model.addAttribute("stock", stock);
            return "stock/StockIn";
        }

        stockInService.executeArrival(stock);

        return "stock/StockInComplete";
    }
    
    // 入荷ID検索画面
    @GetMapping("/StockInEditSearch")
    public String stockInEditSearchForm(Model model) {
        model.addAttribute("stock", new StockInEntity());
        return "stock/StockInEditSearch";
    }

    @GetMapping("/StockInEdit")
    public String stockInEditGet() {
        return "redirect:/stock/StockInEditSearch";
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
    
    @GetMapping("/StockInEditConfirm")
    public String stockInEditConfirmGet() {
        return "redirect:/stock/StockInEditSearch";
    }
    
    @PostMapping("/StockInEditConfirm")
    public String stockInEditConfirm(@ModelAttribute StockInEntity stock,
                                     Model model,
                                     HttpSession session) {

        // ★① 商品取得（論理削除込み推奨）
        ProductEntity product =
                productService.findByIdAndDeletedFalse(stock.getProductId());

        if (product == null) {
            return "redirect:/stock/StockInEditSearch";
        }

        Integer qty = stock.getQuantity();
        if (qty == null) qty = 0;

        LocalDate today = LocalDate.now();

        // ★② 数量チェック
        if (qty <= 0) {
            model.addAttribute("errorMessage", "入荷数は1以上で入力してください");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);
            return "stock/StockInEdit";
        }

        // ★③ 未来日チェック
        if (stock.getArrivalDate() != null &&
            stock.getArrivalDate().isAfter(today)) {

            model.addAttribute("errorMessage", "入荷日は未来日を指定できません");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);
            return "stock/StockInEdit";
        }

        // ★④ 在庫チェック（Service）
        boolean result = stockInService.canEdit(stock);

        if (!result) {
            model.addAttribute("errorMessage", "在庫が不足するためこの変更はできません");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);
            return "stock/StockInEdit";
        }

        // ★⑤ 正常系
        model.addAttribute("stock", stock);
        model.addAttribute("product", product);

        // ★⑥ Confirmフラグ（Disposalと統一）
        session.setAttribute("stockInEditConfirm", true);

        return "stock/StockInEditConfirm";
    }
    
    @GetMapping("/StockInEdit/{id}")
    public String stockInEdit(@PathVariable Integer id,
                              Model model,
                              HttpSession session) {

        // ★① 入口チェック（直打ち防止）※Disposalと統一
        Boolean fromSearch =
                (Boolean) session.getAttribute("fromStockInEditSearch");

        if (fromSearch == null || !fromSearch) {
            return "redirect:/stock/StockInEditSearch";
        }

        // ★使い回し防止
        session.removeAttribute("fromStockInEditSearch");

        // ★② 入荷データ取得
        StockInEntity stock = stockInService.findById(id);

        if (stock == null) {
            return "redirect:/stock/StockInEditSearch";
        }

        // ★③ 商品取得（論理削除込みに統一）
        ProductEntity product =
                productService.findByIdAndDeletedFalse(stock.getProductId());

        if (product == null) {
            return "redirect:/stock/StockInEditSearch";
        }

        // ★④ 必要なら商品一覧（select用）
        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("stock", stock);
        model.addAttribute("product", product);
        model.addAttribute("productList", productList);

        return "stock/StockInEdit";
    }
    
    @GetMapping("/StockInEditBack/{id}")
    public String stockInEditBack(@PathVariable Integer id, Model model) {

        StockInEntity stock = stockInService.findById(id);

        if (stock == null) {
            return "redirect:/stock/StockInEditSearch";
        }

        ProductEntity product =
                productService.findByIdAndDeletedFalse(stock.getProductId());

        if (product == null) {
            return "redirect:/stock/StockInEditSearch";
        }

        model.addAttribute("stock", stock);
        model.addAttribute("product", product);

        return "stock/StockInEdit";
    }
    
    @GetMapping("/save")
    public String stockInEditSaveGet() {
        return "redirect:/stock/StockInEditSearch";
    }
    
    @PostMapping("/save")
    public String stockInEditSave(@ModelAttribute StockInEntity stock,
                                   Model model,
                                   HttpSession session) {

        Boolean flag = (Boolean) session.getAttribute("stockInEditConfirm");

        if (flag == null || !flag) {
            return "redirect:/stock/StockInEditSearch";
        }

        session.removeAttribute("stockInEditConfirm");

        // ★① 商品取得（論理削除込み）
        ProductEntity product =
                productService.findByIdAndDeletedFalse(stock.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "商品が存在しません");
            model.addAttribute("stock", stock);
            return "stock/StockInEditSearch";
        }

        // ★② 編集元データ再取得（重要）
        StockInEntity dbStock = stockInService.findById(stock.getId());

        if (dbStock == null) {
            model.addAttribute("errorMessage", "対象データが存在しません");
            return "stock/StockInEditSearch";
        }

        // ★③ 7日制限（最終防衛）
        LocalDateTime limit = LocalDateTime.now().minusDays(7);

        if (dbStock.getCreatedAt() != null &&
            dbStock.getCreatedAt().isBefore(limit)) {

            model.addAttribute("errorMessage", "登録から7日経過したため編集できません");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);

            return "stock/StockInEditSearch";
        }

        // ★④ 数量チェック
        if (stock.getQuantity() == null || stock.getQuantity() <= 0) {
            model.addAttribute("errorMessage", "入荷数は1以上で入力してください");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);

            return "stock/StockInEdit";
        }

        // ★⑤ 未来日チェック
        LocalDate today = LocalDate.now();

        if (stock.getArrivalDate() != null &&
            stock.getArrivalDate().isAfter(today)) {

            model.addAttribute("errorMessage", "入荷日は未来日を指定できません");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);

            return "stock/StockInEdit";
        }

        // ★⑥ 更新処理
        stockInService.update(stock);

        return "stock/StockInEditComplete";
    }
    
    // 入荷削除検索画面
    @GetMapping("/StockInDeleteSearch")
    public String stockInDeleteSearchForm(Model model) {
        model.addAttribute("stock", new StockInEntity());
        return "stock/StockInDeleteSearch";
    }

    @GetMapping("/StockInDeleteConfirm")
    public String stockInDeleteConfirmGet() {
        return "redirect:/stock/StockInDeleteSearch";
    }
    
    @PostMapping("/StockInDeleteConfirm")
    public String stockInDeleteConfirm(@RequestParam Integer id,
                                        Model model,
                                        HttpSession session) {

        // ★① 入荷データ取得
        StockInEntity stock = stockInService.findById(id);

        if (stock == null) {
            model.addAttribute("errorMessage", "入荷ID " + id + " は存在しません");
            return "stock/StockInDeleteSearch";
        }

        // ★② 商品取得（論理削除込みに統一）
        ProductEntity product =
                productService.findByIdAndDeletedFalse(stock.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "対象商品が存在しません");
            return "stock/StockInDeleteSearch";
        }

        // ★③ 7日制限チェック
        if (stock.getCreatedAt() != null &&
            stock.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {

            model.addAttribute("errorMessage", "登録から7日経過したため削除できません");
            return "stock/StockInDeleteSearch";
        }

        // ★④ 在庫チェック（削除時にマイナスになるのを防ぐ）
        int currentStock = product.getStock() == null ? 0 : product.getStock();
        int qty = stock.getQuantity() == null ? 0 : stock.getQuantity();

        int newStock = currentStock - qty;

        if (newStock < 0) {
            model.addAttribute("errorMessage",
                    "在庫不足のため削除できません（在庫がマイナスになります）");
            return "stock/StockInDeleteSearch";
        }

        // ★⑤ 正常系
        model.addAttribute("stock", stock);
        model.addAttribute("product", product);

        // ★⑥ セッションフラグ（Disposalと統一）
        session.setAttribute("stockInDeleteConfirm", true);

        return "stock/StockInDeleteConfirm";
    }
        

    @GetMapping("/StockInDeleteComplete")
    public String stockInDeleteCompleteGet() {
        return "redirect:/stock/StockInDeleteSearch";
    }
    
    @PostMapping("/StockInDeleteComplete")
    public String stockInDeleteComplete(@RequestParam Integer id,
                                         Model model,
                                         HttpSession session) {

        // ★① セッションガード（Disposalと統一）
        Boolean flag = (Boolean) session.getAttribute("stockInDeleteConfirm");

        if (flag == null || !flag) {
            return "redirect:/stock/StockInDeleteSearch";
        }

        session.removeAttribute("stockInDeleteConfirm");

        // ★② 入荷データ取得
        StockInEntity stock = stockInService.findById(id);

        if (stock == null) {
            model.addAttribute("errorMessage", "入荷ID " + id + " は存在しません");
            return "stock/StockInDeleteSearch";
        }

        // ★③ 商品取得（論理削除込み）
        ProductEntity product =
                productService.findByIdAndDeletedFalse(stock.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "対象商品が存在しません");
            return "stock/StockInDeleteSearch";
        }

        // ★④ 7日制限（最終防衛）
        if (stock.getCreatedAt() != null &&
            stock.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {

            model.addAttribute("errorMessage", "登録から7日経過したため削除できません");
            return "stock/StockInDeleteSearch";
        }

        // ★⑤ 削除実行（Serviceに統一）
        boolean result = stockInService.executeDelete(id);

        if (!result) {
            model.addAttribute("errorMessage",
                    "在庫不足のため削除できません（在庫がマイナスになります）");
            model.addAttribute("stock", stock);
            model.addAttribute("product", product);
            return "stock/StockInDeleteSearch";
        }

        // ★⑥ 成功
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
