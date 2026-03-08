package com.example.demo.stock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

        ProductEntity productByJan = null;
        ProductEntity productById = null;

        // ④ どちらも入力していない
        if ((janCode == null || janCode.isEmpty()) && sales.getProductId() == null) {
            model.addAttribute("errorMessage", "商品を選択するかJANコードを入力してください");
            model.addAttribute("sales", new SalesEntity());
            model.addAttribute("productList", productService.findAll());
            return "sales/SalesRegister";
        }

        // JANコード検索
        if (janCode != null && !janCode.isEmpty()) {
            productByJan = productService.findByJanCode(janCode);
        }

        // 商品ID検索
        if (sales.getProductId() != null) {
            productById = productService.findById(sales.getProductId());
        }

        // ① JANコード存在しない
        if (janCode != null && !janCode.isEmpty() && productByJan == null) {
            model.addAttribute("errorMessage", "そのJANコードの商品は存在しません");
            model.addAttribute("sales", new SalesEntity());
            model.addAttribute("productList", productService.findAll());
            return "sales/SalesRegister";
        }

        // ②③ 両方入力
        if (productByJan != null && productById != null) {

            // ③ 不一致
            if (!productByJan.getId().equals(productById.getId())) {
                model.addAttribute("errorMessage", "商品名とJANコードが一致しません");
                model.addAttribute("sales", new SalesEntity());
                model.addAttribute("productList", productService.findAll());
                return "sales/SalesRegister";
            }

            // ② 一致
            sales.setProductId(productByJan.getId());
            model.addAttribute("product", productByJan);
            model.addAttribute("sales", sales);
            return "sales/SalesConfirm";
        }

        // JANのみ
        if (productByJan != null) {
            sales.setProductId(productByJan.getId());
            model.addAttribute("product", productByJan);
            model.addAttribute("sales", sales);
            return "sales/SalesConfirm";
        }

        // 商品名のみ
        if (productById != null) {
            model.addAttribute("product", productById);
            model.addAttribute("sales", sales);
            return "sales/SalesConfirm";
        }

        model.addAttribute("errorMessage", "商品が見つかりません");
        model.addAttribute("sales", new SalesEntity());
        model.addAttribute("productList", productService.findAll());
        return "sales/SalesRegister";
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

    // 販売数保存
    @PostMapping("/SalesSave")
    public String salesSave(@ModelAttribute SalesEntity sales) {
        salesService.save(sales);
        return "sales/SalesComplete";
    }
}