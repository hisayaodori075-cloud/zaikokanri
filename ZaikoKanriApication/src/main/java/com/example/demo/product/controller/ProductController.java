package com.example.demo.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.service.ProductService;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    // ---------------- 新規登録 ----------------
    @GetMapping("/newproduct")
    public String showForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        return "product/newproduct";
    }

    @PostMapping("/confirm")
    public String confirm(@ModelAttribute ProductEntity product, Model model) {

        // ★バリデーション追加
        if (product.getPurchasePrice() != null && product.getPurchasePrice() < 0) {
            model.addAttribute("error", "仕入価格にマイナスは入力できません");
            return "product/newproduct";
        }

        if (product.getPrice() != null && product.getPrice() < 0) {
            model.addAttribute("error", "売価にマイナスは入力できません");
            return "product/newproduct";
        }

        if (product.getJanCode() != null &&
                product.getJanCode().contains("-")) {
                model.addAttribute("error", "JANコードにマイナスは使用できません");
                model.addAttribute("product", product); // ★これ追加
                return "product/newproduct";
            }

        model.addAttribute("product", product);
        return "product/confirm";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ProductEntity product, Model model) {

        // ★バリデーション追加
        if (product.getPurchasePrice() != null && product.getPurchasePrice() < 0) {
            model.addAttribute("error", "仕入価格にマイナスは入力できません");
            return "product/newproduct";
        }

        if (product.getPrice() != null && product.getPrice() < 0) {
            model.addAttribute("error", "売価にマイナスは入力できません");
            return "product/newproduct";
        }

        if (product.getJanCode() != null &&
                product.getJanCode().contains("-")) {
                model.addAttribute("error", "JANコードにマイナスは使用できません");
                model.addAttribute("product", product); // ★これ追加
                return "product/newproduct";
            }

        productService.save(product);
        model.addAttribute("product", product);
        return "product/complete";
    }

    // ---------------- 編集 ----------------
    @GetMapping("/ProductEdit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        ProductEntity product = productService.findById(id);
        if (product == null) {
            return "redirect:/product/ProductMasterList"; // 存在しなければ一覧へ
        }
        model.addAttribute("product", product);
        return "product/ProductEdit";
    }

    @PostMapping("/ProductEdit/{id}")
    public String editSubmit(@ModelAttribute ProductEntity product, Model model) {

        // ★バリデーション追加
        if (product.getPurchasePrice() != null && product.getPurchasePrice() < 0) {
            model.addAttribute("error", "仕入価格にマイナスは入力できません");
            return "product/ProductEdit";
        }

        if (product.getPrice() != null && product.getPrice() < 0) {
            model.addAttribute("error", "売価にマイナスは入力できません");
            return "product/ProductEdit";
        }

        if (product.getJanCode() != null &&
                product.getJanCode().contains("-")) {
                model.addAttribute("error", "JANコードにマイナスは使用できません");
                model.addAttribute("product", product); // ★これ追加
                return "product/newproduct";
            }

        model.addAttribute("product", product);
        return "product/ProductEditConfirm";
    }

    @PostMapping("/ProductEditComplete")
    public String editComplete(@ModelAttribute ProductEntity product, Model model) {

        // ★バリデーション追加
        if (product.getPurchasePrice() != null && product.getPurchasePrice() < 0) {
            model.addAttribute("error", "仕入価格にマイナスは入力できません");
            return "product/ProductEdit";
        }

        if (product.getPrice() != null && product.getPrice() < 0) {
            model.addAttribute("error", "売価にマイナスは入力できません");
            return "product/ProductEdit";
        }

        if (product.getJanCode() != null &&
                product.getJanCode().contains("-")) {
                model.addAttribute("error", "JANコードにマイナスは使用できません");
                model.addAttribute("product", product); // ★これ追加
                return "product/newproduct";
            }

        // DBから元データ取得
        ProductEntity dbProduct = productService.findById(product.getId());

        // 必要な項目だけ上書き
        dbProduct.setJanCode(product.getJanCode());
        dbProduct.setMakerName(product.getMakerName());
        dbProduct.setPurchasePrice(product.getPurchasePrice());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setSalesStatus(product.getSalesStatus());

        productService.save(dbProduct);

        return "product/ProductEditComplete";
    }

    // ---------------- 削除 ----------------
    @GetMapping("/ProductDelete/{id}")
    public String deleteForm(@PathVariable Integer id, Model model) {
        ProductEntity product = productService.findById(id);
        if (product == null) {
            return "redirect:/product/ProductMasterList";
        }
        model.addAttribute("product", product);
        return "product/ProductDelete";
    }

    @PostMapping("/ProductDeleteComplete/{id}")
    public String deleteComplete(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return "/product/ProductDeleteComplete";
    }

    // ---------------- 一覧 ----------------
    @GetMapping("/ProductMasterList")
    public String productMasterList(Model model) {
        List<ProductEntity> productList = productService.findAll();
        model.addAttribute("productList", productList);
        return "product/ProductMasterList";
    }
}