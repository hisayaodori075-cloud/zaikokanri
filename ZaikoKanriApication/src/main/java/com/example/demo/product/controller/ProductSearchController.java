package com.example.demo.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.form.ProductSearchForm;
import com.example.demo.product.service.ProductService;

@Controller
public class ProductSearchController {

    @Autowired
    private ProductService productService;

    @GetMapping("/product/ProductSearch")
    public String showSearchForm(Model model) {
        model.addAttribute("productSearchForm", new ProductSearchForm());
        return "product/ProductSearch";
    }

    @GetMapping("/product/ProductSearchConfirm")
    public String search(
            @ModelAttribute ProductSearchForm form,
            Model model) {

        // 検索条件がすべて空かチェック
        if ((form.getJanCode() == null || form.getJanCode().isEmpty()) &&
            (form.getMakerName() == null || form.getMakerName().isEmpty()) &&
            (form.getProductName() == null || form.getProductName().isEmpty()) &&
            form.getPurchasePrice() == null &&
            form.getPrice() == null &&
            (form.getSalesStatus() == null || form.getSalesStatus().isEmpty())) {

            model.addAttribute("errorMessage", "検索条件を1つ以上入力してください");
            model.addAttribute("productSearchForm", form); // ★これあると親切
            return "product/ProductSearch";
        }

        List<ProductEntity> result = productService.search(form);

        // ★ここ追加（今回の本題）
        if (result.isEmpty()) {
            model.addAttribute("errorMessage", "該当する商品は存在しません");
            model.addAttribute("productSearchForm", form); // ★入力保持
            return "product/ProductSearch";
        }

        model.addAttribute("productList", result);

        return "product/ProductSearchConfirm";
    }
}