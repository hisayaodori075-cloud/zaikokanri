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
    
    @GetMapping("/stock/DisposalMenu")
    public String DisposalMenuForm() {
        return "stock/DisposalMenu";
    }
    
    // 検索画面表示
    @GetMapping("/stock/DisposalEditSearch")
    public String showEditSearch() {
        return "stock/DisposalEditSearch";
    }

    // 検索実行
    @PostMapping("/stock/DisposalEdit")
    public String editDisposal(@RequestParam("disposalId") Integer id, Model model) {

        DisposalEntity disposal = disposalService.findById(id);

        if (disposal != null) {
            // productId から商品情報を取得して Model にセット
            ProductEntity product = productService.findById(disposal.getProductId());
            if (product == null) {
                model.addAttribute("errorMessage", "廃棄ID " + id + " の対象商品が存在しません");
                return "stock/DisposalEditSearch";
            }

            model.addAttribute("disposal", disposal);
            model.addAttribute("product", product);

            return "stock/DisposalEdit";
            
        } else {
            model.addAttribute("errorMessage", "廃棄ID " + id + " は存在しません");
            return "stock/DisposalEditSearch";
        }
    }
    
 // 編集確認画面
    @PostMapping("/stock/DisposalEditConfirm")
    public String disposalEditConfirm(@ModelAttribute DisposalEntity disposal, Model model) {

        // 商品情報を取得
        ProductEntity product = productService.findById(disposal.getProductId());

        // 在庫が足りるかチェック（必要であれば）
        if (disposal.getQuantity() > product.getStock()) {
            model.addAttribute("errorMessage", "廃棄数が在庫数を超えています。");
            model.addAttribute("disposal", disposal);
            model.addAttribute("product", product);
            return "stock/DisposalEdit"; // 戻して修正させる
        }

        model.addAttribute("disposal", disposal);
        model.addAttribute("product", product);

        return "stock/DisposalEditConfirm"; // 確認画面へ
    }
    
    // 廃棄編集画面（入力画面に戻る）
    @GetMapping("/stock/DisposalEdit/{id}")
    public String disposalEdit(@PathVariable Integer id, Model model) {
        DisposalEntity disposal = disposalService.findById(id);

        if (disposal == null) {
            // 該当レコードがなければ検索画面に戻す
            model.addAttribute("errorMessage", "廃棄ID " + id + " は存在しません");
            return "stock/DisposalEditSearch";
        }

        // 対応する商品情報も取得
        ProductEntity product = productService.findById(disposal.getProductId());

        if (product == null) {
            // 商品情報が存在しない場合も検索画面に戻す
            model.addAttribute("errorMessage", "廃棄ID " + id + " の対象商品が存在しません");
            return "stock/DisposalEditSearch";
        }

        // 商品リストも取得しておく（編集画面で select を生成する場合に必要）
        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("disposal", disposal);
        model.addAttribute("product", product);
        model.addAttribute("productList", productList);

        return "stock/DisposalEdit"; // 入力画面のHTML
    }
    
    @PostMapping("/stock/DisposalEditSave")
    public String disposalEditSave(@ModelAttribute DisposalEntity disposal, Model model) {

        // 編集前の廃棄データを取得
        DisposalEntity oldDisposal = disposalService.findById(disposal.getId());
        if (oldDisposal == null) {
            model.addAttribute("errorMessage", "廃棄ID " + disposal.getId() + " が存在しません");
            return "stock/DisposalEditConfirm";
        }

        // 差分を計算（新しい廃棄数 - 古い廃棄数）
        int diff = disposal.getQuantity() - oldDisposal.getQuantity();

        // 対象商品を取得
        ProductEntity product = productService.findById(disposal.getProductId());
        if (product == null) {
            model.addAttribute("errorMessage", "対象の商品が存在しません");
            return "stock/DisposalEditConfirm";
        }

        // 在庫に差分を反映
        int newStock = product.getStock() - diff;
        if (newStock < 0) {
            model.addAttribute("errorMessage", "廃棄数が在庫を超えています");
            model.addAttribute("product", product);
            model.addAttribute("disposal", disposal);
            return "stock/DisposalEditConfirm";
        }
        product.setStock(newStock);
        productService.save(product); // 在庫更新

        // 廃棄情報を更新
        disposalService.save(disposal);

        return "stock/DisposalEditComplete"; // 完了画面に遷移
    }
}