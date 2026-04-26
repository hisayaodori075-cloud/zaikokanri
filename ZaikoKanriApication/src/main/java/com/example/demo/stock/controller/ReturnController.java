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

        // 商品ID検索
        if (productId != null) {
            productList = productList.stream()
                    .filter(p -> p.getId().equals(productId))
                    .toList();
        }

        // ★エラーメッセージ統一
        if (productList.isEmpty()) {
            model.addAttribute("errorMessage", "一致する商品がありません");
        }

        // ★ここも完全統一
        model.addAttribute("productList", productList);
        model.addAttribute("allProducts", allProducts);

        return "stock/ReturnRegister";
    }

 // 返品数入力画面
    @GetMapping("/stock/ReturnInput/{id}")
    public String returnInput(@PathVariable Integer id, Model model) {

        // ★削除されていない商品だけ取得（統一）
        ProductEntity product = productService.findByIdAndDeletedFalse(id);

        if (product == null) {
            // 存在しない or 論理削除済は弾く
            return "redirect:/stock/ReturnRegister";
        }

        // ★ReturnEntity に productId をセット
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

 // ★GET直打ち対策
    @GetMapping("/stock/ReturnConfirm")
    public String returnConfirmGet() {
        return "redirect:/stock/ReturnRegister";
    }

    @PostMapping("/stock/ReturnConfirm")
    public String returnConfirm(
            @ModelAttribute ReturnEntity returnData,
            Model model,
            HttpSession session) {

        // ★削除されていない商品だけ取得
        ProductEntity product =
                productService.findByIdAndDeletedFalse(returnData.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "商品が存在しません");
            return "stock/ReturnInput";
        }

        // ★在庫チェック
        Integer stock = product.getStock();
        if (stock == null) stock = 0;

        Integer qty = returnData.getReturnQuantity();
        if (qty == null) qty = 0;

        if (qty > stock) {
            model.addAttribute("errorMessage", "返品数が在庫数を超えています");
            model.addAttribute("product", product);
            model.addAttribute("returnData", returnData);
            return "stock/ReturnInput";
        }

        // ★数量チェック
        if (qty <= 0) {
            model.addAttribute("errorMessage", "返品数は1以上で入力してください");
            model.addAttribute("product", product);
            model.addAttribute("returnData", returnData);
            return "stock/ReturnInput";
        }

        // ★未来日チェック
        if (returnData.getReturnDate() != null &&
            returnData.getReturnDate().isAfter(LocalDate.now())) {

            model.addAttribute("errorMessage", "返品日は未来日を指定できません");
            model.addAttribute("product", product);
            model.addAttribute("returnData", returnData);
            return "stock/ReturnInput";
        }

        model.addAttribute("product", product);
        model.addAttribute("returnData", returnData);

        // ★セッション制御（統一）
        session.setAttribute("returnConfirm", true);

        return "stock/ReturnConfirm";
    }

 // ★GET直打ち対策
    @GetMapping("/stock/ReturnSave")
    public String returnSaveGet() {
        return "redirect:/stock/ReturnRegister";
    }

    // 返品保存処理
    @PostMapping("/stock/ReturnSave")
    public String returnSave(
            @ModelAttribute ReturnEntity returnData,
            Model model,
            HttpSession session) {

        // ★セッションチェック（最重要）
        Boolean flag = (Boolean) session.getAttribute("returnConfirm");

        if (flag == null || !flag) {
            return "redirect:/stock/ReturnRegister";
        }

        // ★削除されていない商品だけ取得
        ProductEntity product =
                productService.findByIdAndDeletedFalse(returnData.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "商品が存在しません");
            return "stock/ReturnInput";
        }

        // ★未来日チェック
        if (returnData.getReturnDate() != null &&
            returnData.getReturnDate().isAfter(LocalDate.now())) {

            model.addAttribute("errorMessage", "返品日は未来日を指定できません");
            model.addAttribute("product", product);
            model.addAttribute("returnData", returnData);
            return "stock/ReturnInput";
        }

        // ★在庫チェック
        Integer stock = product.getStock();
        if (stock == null) stock = 0;

        Integer qty = returnData.getReturnQuantity();
        if (qty == null) qty = 0;

        if (qty > stock) {
            model.addAttribute("errorMessage", "返品数が在庫数を超えています");
            model.addAttribute("product", product);
            model.addAttribute("returnData", returnData);
            return "stock/ReturnInput";
        }

        // ★数量チェック
        if (qty <= 0) {
            model.addAttribute("errorMessage", "返品数は1以上で入力してください");
            model.addAttribute("product", product);
            model.addAttribute("returnData", returnData);
            return "stock/ReturnInput";
        }

        // ★セッション削除（再送防止）
        session.removeAttribute("returnConfirm");

        // ★登録処理
        returnService.executeReturn(returnData);

        // ★完了画面用（統一）
        model.addAttribute("productName",
            product != null ? product.getProductName() : "");

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

 // ★GET直打ち対策
    @GetMapping("/stock/ReturnDeleteConfirm")
    public String returnDeleteConfirmGet() {
        return "redirect:/stock/ReturnDeleteSearch";
    }

    @PostMapping("/stock/ReturnDeleteConfirm")
    public String returnDeleteConfirm(
            @RequestParam("returnId") Integer id,
            Model model,
            HttpSession session) {

        ReturnEntity returnData = returnService.findById(id);

        // ★① 返品の論理削除チェック
        if (returnData == null || returnData.isDeleted()) {
            model.addAttribute("errorMessage", "返品ID " + id + " は存在しません");
            return "stock/ReturnDeleteSearch";
        }

        // ★② 商品も論理削除込みで取得
        ProductEntity product =
                productService.findByIdAndDeletedFalse(returnData.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "対象商品が存在しません");
            return "stock/ReturnDeleteSearch";
        }

        // ★③ 7日制限チェック
        if (returnData.getCreatedAt() != null &&
            returnData.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {

            model.addAttribute("errorMessage", "登録から7日経過したため削除できません");
            return "stock/ReturnDeleteSearch";
        }

        model.addAttribute("returnData", returnData);
        model.addAttribute("product", product);

        // ★セッション制御（統一）
        session.setAttribute("returnDeleteConfirm", true);

        return "stock/ReturnDeleteConfirm";
    }

 // ★GET直打ち対策
    @GetMapping("/stock/ReturnDeleteComplete")
    public String returnDeleteCompleteGet() {
        return "redirect:/stock/ReturnDeleteSearch";
    }

    // 返品削除実行（論理削除）
    @PostMapping("/stock/ReturnDeleteComplete")
    public String returnDeleteComplete(
            @ModelAttribute ReturnEntity returnData,
            Model model,
            HttpSession session) {

        // ★セッションチェック（最重要）
        Boolean flag = (Boolean) session.getAttribute("returnDeleteConfirm");

        if (flag == null || !flag) {
            return "redirect:/stock/ReturnDeleteSearch";
        }

        // ★セッション削除（再送防止）
        session.removeAttribute("returnDeleteConfirm");

        ReturnEntity target = returnService.findById(returnData.getId());

        // ★① 存在＋論理削除チェック
        if (target == null || target.isDeleted()) {
            model.addAttribute("errorMessage", "返品ID " + returnData.getId() + " は存在しません");
            return "stock/ReturnDeleteSearch";
        }

        // ★② 最終防衛（7日制限）
        if (target.getCreatedAt() != null &&
            target.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {

            model.addAttribute("errorMessage", "登録から7日経過したため削除できません");
            return "stock/ReturnDeleteSearch";
        }

        // ★論理削除実行
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