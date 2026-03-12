package com.example.demo.stock.controller;

import java.util.List;

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
    
    // 販売数入力画面
    @GetMapping("/SalesRegister")
    public String salesRegisterForm(Model model) {
        model.addAttribute("sales", new SalesEntity());

        // 商品一覧を取得してプルダウン表示用に渡す
        List<ProductEntity> productList = productService.findAll();
        model.addAttribute("productList", productList);

        return "sales/SalesRegister";
    }
    
    @PostMapping("/SalesRegister")
    public String search(
            @ModelAttribute SalesEntity sales,
            @RequestParam(required = false) String janCode,
            Model model) {

        // ① 商品を特定
        ProductEntity productByJan = null;
        ProductEntity productById = null;
        ProductEntity product = null;

        if (janCode != null && !janCode.isEmpty()) {
            productByJan = productService.findByJanCode(janCode);
        }

        if (sales.getProductId() != null) {
            productById = productService.findById(sales.getProductId());
        }

        // ② product決定
        if (productByJan != null && productById != null) {
            if (!productByJan.getId().equals(productById.getId())) {
                // 商品不一致エラー
            }
            product = productByJan;
        } else if (productByJan != null) {
            product = productByJan;
        } else if (productById != null) {
            product = productById;
        }

        if (product == null) {
            // 商品が見つからないエラー
        }

        // ③ Confirm画面へ
        sales.setProductId(product.getId());
        model.addAttribute("product", product);
        model.addAttribute("sales", sales);

        return "sales/SalesConfirm";
    }

    // 販売数登録確認画面
    @GetMapping("/SalesConfirm")
    public String salesConfirmForm(Model model) {
        return "sales/SalesConfirm";
    }
    
    @PostMapping("/SalesConfirm")
    public String salesConfirm(@ModelAttribute SalesEntity sales, Model model) {
        ProductEntity product = productService.findById(sales.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "その商品IDは存在しません");
            model.addAttribute("sales", sales);
            return "sales/SalesManagement";
        }

        model.addAttribute("sales", sales);
        model.addAttribute("product", product);

        return "sales/SalesConfirm";
    }
    
    @PostMapping("/SalesConfirm2")
    public String confirm(@ModelAttribute SalesEntity sales, Model model) {

        ProductEntity product = productService.findById(sales.getProductId());

        if (product == null) {
            model.addAttribute("errorMessage", "商品が見つかりません");
            return "sales/SalesManagement";
        }

        model.addAttribute("sales", sales);
        model.addAttribute("product", product);

        return "sales/SalesConfirm2";  // 今作っているHTML
    }

    @PostMapping("/SalesSave")
    public String salesSave(@ModelAttribute SalesEntity sales, Model model) {

        ProductEntity product = productService.findById(sales.getProductId());

        // ★ 販売数未入力チェック
        if (sales.getSalesQuantity() == null) {
            model.addAttribute("errorMessage", "販売数を入力してください");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);
            return "sales/SalesConfirm";
        }

        // ★ 在庫不足チェック
        if (product.getStock() < sales.getSalesQuantity()) {
            model.addAttribute("errorMessage", "在庫が不足しています");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);
            return "sales/SalesConfirm";
        }

        // 保存 & 在庫更新
        salesService.save(sales);
        product.setStock(product.getStock() - sales.getSalesQuantity());
        productService.save(product);

        return "sales/SalesComplete";
    }
    
    // 販売数修正
    @GetMapping("/SalesEditSearch")
    public String salesEditSearchForm(Model model) {  
        return "sales/SalesEditSearch";
    }
    
    @PostMapping("/SalesEdit")
    public String salesInEdit(@RequestParam Integer id, Model model) {
        SalesEntity sales = salesService.findById(id);
        // ★ここ追加
        if (sales == null) {
            model.addAttribute("errorMessage", "そのIDの販売数データは存在しません");
            model.addAttribute("sales", new SalesEntity());
            return "sales/SalesEditSearch";
        }

        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("sales", sales);
        model.addAttribute("productList", productList);
        return "sales/SalesEdit";
    }
    
    @PostMapping("/SalesEditConfirm")
    public String salesEditConfirm(@ModelAttribute SalesEntity sales, Model model) {

        ProductEntity product = productService.findById(sales.getProductId());

        model.addAttribute("sales", sales);
        model.addAttribute("product", product);

        return "sales/SalesEditConfirm";
    }
    
    @GetMapping("/SalesDeleteSearch")
    public String salesDeleteSearchForm(Model model) {
        return "sales/SalesDeleteSearch";
    }
    
    @PostMapping("/SalesDeleteSearch")
    public String salesDeleteConfirm(@RequestParam Integer id, Model model) {
        SalesEntity sales = salesService.findById(id);

        if (sales == null || sales.isDeleted()) {  // ← 論理削除済みも存在しない扱い
            model.addAttribute("errorMessage", "そのIDの販売数データは存在しません");
            return "sales/SalesDeleteSearch";
        }

        ProductEntity product = productService.findById(sales.getProductId());

        model.addAttribute("sales", sales);
        model.addAttribute("product", product);

        return "sales/SalesDeleteConfirm";
    }
    
    @PostMapping("/SalesDeleteBack")
    public String salesDeleteBack() {
        return "sales/SalesDeleteSearch";
    }
    
    @PostMapping("/SalesDeleteComplete/{id}")
    public String deleteComplete(@PathVariable Integer id) {
        salesService.delete(id); // ← Service 側で論理削除処理
        return "sales/SalesDeleteComplete";
    }
    
    @PostMapping("/SalesEditSave")
    public String salesEditSave(@ModelAttribute SalesEntity sales) {
        salesService.save(sales);
        return "sales/SalesEditComplete";
    }
}