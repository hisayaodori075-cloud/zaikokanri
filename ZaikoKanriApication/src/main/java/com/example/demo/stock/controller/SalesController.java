package com.example.demo.stock.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.service.ProductService;
import com.example.demo.stock.entity.SalesEntity;
import com.example.demo.stock.service.SalesService;

@Controller
@RequestMapping("/sales")
public class SalesController {

    @Autowired
    private SalesService salesService;

    @Autowired
    private ProductService productService;

    @GetMapping("/SalesManagement")
    public String salesManagementForm(Model model) {
        return "sales/SalesManagement";
    }

    // ---------------- 販売数入力 ----------------
    @GetMapping("/SalesRegister")
    public String salesRegister(
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

        // 商品ID検索
        if (productId != null) {
            productList = productList.stream()
                    .filter(p -> p.getId().equals(productId))
                    .toList();
        }

        // ★ここ修正（入荷と統一）
        if (productList.isEmpty()) {
            model.addAttribute("errorMessage", "一致する商品がありません");
        }

        model.addAttribute("productList", productList);
        model.addAttribute("allProducts", allProducts);

        return "sales/SalesRegister";
    }
    
    @GetMapping("/SalesInput/{id}")
    public String salesInput(@PathVariable Integer id, Model model) {

        // ★削除されていない商品だけ取得（入荷と統一）
        ProductEntity product = productService.findByIdAndDeletedFalse(id);

        // ★存在しない or 論理削除済は弾く
        if (product == null) {
            return "redirect:/sales/SalesRegister";
        }

        // SalesEntity に productId をセット
        SalesEntity sales = new SalesEntity();
        sales.setProductId(product.getId());

        model.addAttribute("product", product);
        model.addAttribute("sales", sales);

        return "sales/SalesInput";
    }
    
    @PostMapping("/SalesInputBack")
    public String salesInputBack(@ModelAttribute SalesEntity sales, Model model) {

        // ★① 商品取得（論理削除込みで統一）
        ProductEntity product =
                productService.findByIdAndDeletedFalse(sales.getProductId());

        // ★② 存在チェック
        if (product == null) {
            return "redirect:/sales/SalesRegister";
        }

        model.addAttribute("sales", sales);
        model.addAttribute("product", product);

        return "sales/SalesInput";
    }
    
    @GetMapping("/SalesConfirm")
    public String salesConfirmGet() {
        return "redirect:/sales/SalesRegister";
    }

    @PostMapping("/SalesConfirm")
    public String confirm(@ModelAttribute SalesEntity sales,
                          Model model,
                          HttpSession session) {

        LocalDate today = LocalDate.now();

        // ★① 商品取得（論理削除込み）
        ProductEntity product =
                productService.findByIdAndDeletedFalse(sales.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "商品が存在しません");
            return "sales/SalesInput";
        }

        // ★② 数量チェック
        Integer qty = sales.getSalesQuantity();
        if (qty == null || qty <= 0) {
            model.addAttribute("errorMessage", "販売数は1以上で入力してください");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);
            return "sales/SalesInput";
        }

        // ★③ 在庫チェック
        Integer stock = product.getStock();
        if (stock == null) stock = 0;

        if (qty > stock) {
            model.addAttribute("errorMessage", "販売数が在庫数を超えています");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);
            return "sales/SalesInput";
        }

        // ★④ 未来日チェック
        if (sales.getSalesDate() != null &&
            sales.getSalesDate().isAfter(today)) {

            model.addAttribute("errorMessage", "販売日は未来日を指定できません");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);
            return "sales/SalesInput";
        }

        // ★⑤ 正常系
        model.addAttribute("sales", sales);
        model.addAttribute("product", product);

        // ★⑥ フラグ（超重要）
        session.setAttribute("salesConfirm", true);

        return "sales/SalesConfirm";
    }
    
    // ---------------- 保存 ----------------
    @GetMapping("/SalesSave")
    public String salesSaveGet() {
        return "redirect:/sales/SalesRegister";
    }

    @PostMapping("/SalesSave")
    public String salesSave(@ModelAttribute SalesEntity sales,
                            Model model,
                            HttpSession session) {

        // ★① 直打ち防止
        Boolean flag = (Boolean) session.getAttribute("salesConfirm");

        if (flag == null || !flag) {
            return "redirect:/sales/SalesRegister";
        }

        // ★② 使い回し防止
        session.removeAttribute("salesConfirm");

        LocalDate today = LocalDate.now();

        // ★③ 商品取得
        ProductEntity product =
                productService.findByIdAndDeletedFalse(sales.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "商品が存在しません");
            return "sales/SalesInput";
        }

        // ★④ 数量チェック（最終防衛）
        Integer qty = sales.getSalesQuantity();
        if (qty == null || qty <= 0) {
            model.addAttribute("errorMessage", "販売数は1以上で入力してください");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);
            return "sales/SalesInput";
        }

        // ★⑤ 在庫チェック（最終防衛）
        Integer stock = product.getStock();
        if (stock == null) stock = 0;

        if (qty > stock) {
            model.addAttribute("errorMessage", "販売数が在庫数を超えています");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);
            return "sales/SalesInput";
        }

        // ★⑥ 未来日チェック
        if (sales.getSalesDate() != null &&
            sales.getSalesDate().isAfter(today)) {

            model.addAttribute("errorMessage", "販売日は未来日を指定できません");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);
            return "sales/SalesInput";
        }

        // ★⑦ 登録
        salesService.executeSales(sales);

        return "sales/SalesComplete";
    }

    // ---------------- 販売修正 ----------------
    @GetMapping("/SalesEditSearch")
    public String salesEditSearchForm(Model model) {

        return "sales/SalesEditSearch";
    }

    @GetMapping("/SalesEdit")
    public String salesEditGet() {
        return "redirect:/sales/SalesEditSearch";
    }

    @PostMapping("/SalesEdit")
    public String salesEdit(@RequestParam Integer id, Model model) {

        // ★① nullチェック（入荷と統一）
        if (id == null) {
            model.addAttribute("errorMessage", "IDが指定されていません");
            model.addAttribute("sales", new SalesEntity());
            return "sales/SalesEditSearch";
        }

        // ★② データ取得
        SalesEntity sales = salesService.findById(id);

        if (sales == null) {
            model.addAttribute("errorMessage", "そのIDの販売データは存在しません");
            model.addAttribute("sales", new SalesEntity());
            return "sales/SalesEditSearch";
        }

        // ★③ 7日制限（入荷と同じ位置に）
        if (sales.getCreatedAt() != null &&
            sales.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {

            model.addAttribute("errorMessage", "登録から7日経過しているため編集できません");
            model.addAttribute("sales", new SalesEntity());
            return "sales/SalesEditSearch";
        }

        // ★④ 商品取得（論理削除対応）
        ProductEntity product =
                productService.findByIdAndDeletedFalse(sales.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "対象商品が存在しません");
            model.addAttribute("sales", new SalesEntity());
            return "sales/SalesEditSearch";
        }

        // ★⑤ 商品リスト（select用）
        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("sales", sales);
        model.addAttribute("product", product);
        model.addAttribute("productList", productList);

        return "sales/SalesEdit";
    }

    @GetMapping("/SalesEditConfirm")
    public String salesEditConfirmGet() {
        return "redirect:/sales/SalesEditSearch";
    }

    @PostMapping("/SalesEditConfirm")
    public String salesEditConfirm(@ModelAttribute SalesEntity sales,
                                   Model model,
                                   HttpSession session) {

        LocalDate today = LocalDate.now();

        // ★① 商品取得（論理削除込み）
        ProductEntity product =
                productService.findByIdAndDeletedFalse(sales.getProductId());

        if (product == null) {
            return "redirect:/sales/SalesEditSearch";
        }

        Integer qty = sales.getSalesQuantity();
        if (qty == null) qty = 0;

        // ★② 数量チェック
        if (qty <= 0) {
            model.addAttribute("errorMessage", "販売数は1以上で入力してください");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);
            return "sales/SalesEdit";
        }

        // ★③ 未来日チェック（順番も入荷に合わせる）
        if (sales.getSalesDate() != null &&
            sales.getSalesDate().isAfter(today)) {

            model.addAttribute("errorMessage", "販売日は未来日を指定できません");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);
            return "sales/SalesEdit";
        }

        // ★④ 在庫チェック（Serviceで判定）
        boolean result = salesService.canEdit(sales);

        if (!result) {
            model.addAttribute("errorMessage", "在庫が不足するためこの変更はできません");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);
            return "sales/SalesEdit";
        }

        // ★⑤ 正常系
        model.addAttribute("sales", sales);
        model.addAttribute("product", product);

        // ★⑥ フラグ（超重要）
        session.setAttribute("salesEditConfirm", true);

        return "sales/SalesEditConfirm";
    }
    
    @GetMapping("/SalesEdit/{id}")
    public String salesEdit(@PathVariable Integer id,
                            Model model,
                            HttpSession session) {

        // ★① 入口チェック（直打ち防止）
        Boolean fromSearch =
                (Boolean) session.getAttribute("fromSalesEditSearch");

        if (fromSearch == null || !fromSearch) {
            return "redirect:/sales/SalesEditSearch";
        }

        // ★使い回し防止
        session.removeAttribute("fromSalesEditSearch");

        // ★② 販売データ取得
        SalesEntity sales = salesService.findById(id);

        if (sales == null) {
            return "redirect:/sales/SalesEditSearch";
        }

        // ★③ 商品取得（論理削除込み）
        ProductEntity product =
                productService.findByIdAndDeletedFalse(sales.getProductId());

        if (product == null) {
            return "redirect:/sales/SalesEditSearch";
        }

        // ★④ 商品一覧（select用）
        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("sales", sales);
        model.addAttribute("product", product);
        model.addAttribute("productList", productList);

        return "sales/SalesEdit";
    }
    
    @GetMapping("/SalesEditBack/{id}")
    public String salesEditBack(@PathVariable Integer id, Model model) {

        SalesEntity sales = salesService.findById(id);

        if (sales == null) {
            return "redirect:/sales/SalesEditSearch";
        }

        ProductEntity product =
                productService.findByIdAndDeletedFalse(sales.getProductId());

        if (product == null) {
            return "redirect:/sales/SalesEditSearch";
        }

        model.addAttribute("sales", sales);
        model.addAttribute("product", product);

        return "sales/SalesEdit";
    }
    
    @GetMapping("/SalesEditSave")
    public String salesEditSaveGet() {
        return "redirect:/sales/SalesEditSearch";
    }

    @PostMapping("/SalesEditSave")
    public String salesEditSave(@ModelAttribute SalesEntity sales,
                               Model model,
                               HttpSession session) {

        Boolean flag = (Boolean) session.getAttribute("salesEditConfirm");

        if (flag == null || !flag) {
            return "redirect:/sales/SalesEditSearch";
        }

        session.removeAttribute("salesEditConfirm");

        // ★① 商品取得
        ProductEntity product =
                productService.findByIdAndDeletedFalse(sales.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "商品が存在しません");
            model.addAttribute("sales", sales);
            return "sales/SalesEditSearch";
        }

        // ★② DBデータ再取得（超重要）
        SalesEntity dbSales = salesService.findById(sales.getId());

        if (dbSales == null) {
            model.addAttribute("errorMessage", "対象データが存在しません");
            return "sales/SalesEditSearch";
        }

        // ★③ 7日制限（最終防衛）
        LocalDateTime limit = LocalDateTime.now().minusDays(7);

        if (dbSales.getCreatedAt() != null &&
            dbSales.getCreatedAt().isBefore(limit)) {

            model.addAttribute("errorMessage", "登録から7日経過したため編集できません");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);

            return "sales/SalesEditSearch";
        }

        // ★④ 数量チェック
        Integer qty = sales.getSalesQuantity();
        if (qty == null) qty = 0;

        if (qty <= 0) {
            model.addAttribute("errorMessage", "販売数は1以上で入力してください");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);

            return "sales/SalesEdit";
        }

        // ★⑤ 未来日チェック
        LocalDate today = LocalDate.now();

        if (sales.getSalesDate() != null &&
            sales.getSalesDate().isAfter(today)) {

            model.addAttribute("errorMessage", "販売日は未来日を指定できません");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);

            return "sales/SalesEdit";
        }

        // ★⑥ 在庫チェック（Service）
        boolean result = salesService.canEdit(sales);

        if (!result) {
            model.addAttribute("errorMessage", "在庫が不足するためこの変更はできません");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);

            return "sales/SalesEdit";
        }

        // ★⑦ 更新
        salesService.update(sales);

        return "sales/SalesEditComplete";
    }
    
    // ---------------- 削除 ----------------
    @GetMapping("/SalesDeleteSearch")
    public String salesDeleteSearchForm(Model model) {

        return "sales/SalesDeleteSearch";
    }

    

    @PostMapping("/SalesDeleteBack")
    public String salesDeleteBack() {

        return "sales/SalesDeleteSearch";
    }
    
    @GetMapping("/SalesDeleteConfirm")
    public String salesDeleteConfirmGet() {
        return "redirect:/sales/SalesDeleteSearch";
    }

    @PostMapping("/SalesDeleteConfirm")
    public String salesDeleteConfirm(@RequestParam Integer id,
                                     Model model,
                                     HttpSession session) {

        // ★① 販売データ取得
        SalesEntity sales = salesService.findById(id);

        if (sales == null) {
            model.addAttribute("errorMessage", "販売ID " + id + " は存在しません");
            return "sales/SalesDeleteSearch";
        }

        // ★② 商品取得（論理削除除外）
        ProductEntity product =
                productService.findByIdAndDeletedFalse(sales.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "対象商品が存在しません");
            return "sales/SalesDeleteSearch";
        }

        // ★③ 7日制限チェック
        if (sales.getCreatedAt() != null &&
            sales.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusDays(7))) {

            model.addAttribute("errorMessage", "登録から7日経過したため削除できません");
            return "sales/SalesDeleteSearch";
        }

        // ★④ 在庫チェック（Salesは戻すので +）
        int currentStock = product.getStock() == null ? 0 : product.getStock();
        int qty = sales.getSalesQuantity() == null ? 0 : sales.getSalesQuantity();

        int newStock = currentStock + qty;

        if (newStock < 0) {
            model.addAttribute("errorMessage",
                    "在庫計算に不整合があるため削除できません");
            return "sales/SalesDeleteSearch";
        }

        // ★⑤ 正常系
        model.addAttribute("sales", sales);
        model.addAttribute("product", product);

        // ★⑥ セッションフラグ
        session.setAttribute("salesDeleteConfirm", true);

        return "sales/SalesDeleteConfirm";
    }

    @GetMapping("/SalesDeleteComplete")
    public String salesDeleteCompleteGet() {
        return "redirect:/sales/SalesDeleteSearch";
    }

    @PostMapping("/SalesDeleteComplete")
    public String salesDeleteComplete(@RequestParam Integer id,
                                      Model model,
                                      HttpSession session) {

        // ★① セッションガード
        Boolean flag = (Boolean) session.getAttribute("salesDeleteConfirm");

        if (flag == null || !flag) {
            return "redirect:/sales/SalesDeleteSearch";
        }

        session.removeAttribute("salesDeleteConfirm");

        // ★② データ取得
        SalesEntity sales = salesService.findById(id);

        if (sales == null) {
            model.addAttribute("errorMessage", "販売ID " + id + " は存在しません");
            return "sales/SalesDeleteSearch";
        }

        // ★③ 商品取得
        ProductEntity product =
                productService.findByIdAndDeletedFalse(sales.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "対象商品が存在しません");
            return "sales/SalesDeleteSearch";
        }

        // ★④ 7日制限（最終防衛）
        if (sales.getCreatedAt() != null &&
            sales.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusDays(7))) {

            model.addAttribute("errorMessage", "登録から7日経過したため削除できません");
            return "sales/SalesDeleteSearch";
        }

        // ★⑤ 削除実行
        boolean result = salesService.executeDelete(id);

        if (!result) {
            model.addAttribute("errorMessage",
                    "削除処理に失敗しました（在庫不整合の可能性）");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);
            return "sales/SalesDeleteSearch";
        }

        // ★⑥ 完了
        return "sales/SalesDeleteComplete";
    }

    
    
 // ---------------- 販売履歴一覧 ----------------
    @GetMapping("/SalesList")
    public String salesList(Model model) {

        List<SalesEntity> salesList = salesService.getSalesList();
        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("salesList", salesList);
        model.addAttribute("productList", productList);

        return "sales/SalesList";
    }
}