package com.example.demo.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.form.ProductSearchForm;
import com.example.demo.product.service.ProductService;

@Controller
public class ProductSearchController {

    @Autowired
    private ProductService productService;

    // 検索画面初期表示
    @GetMapping("/product/ProductSearch")
    public String showSearchForm(Model model) {
        model.addAttribute("productSearchForm", new ProductSearchForm());
        return "product/ProductSearch";
    }

    // 検索実行
    @GetMapping("/product/ProductSearchConfirm")
    public String search(@ModelAttribute ProductSearchForm form,
                         Model model) {

        boolean isEmpty =
                (form.getJanCode() == null || form.getJanCode().isEmpty()) &&
                (form.getMakerName() == null || form.getMakerName().isEmpty()) &&
                (form.getProductName() == null || form.getProductName().isEmpty()) &&
                (form.getPurchasePriceMin() == null &&
                 form.getPurchasePriceMax() == null) &&
                (form.getPriceMin() == null &&
                 form.getPriceMax() == null) &&
                (form.getSalesStatus() == null || form.getSalesStatus().isEmpty());

        if (isEmpty) {
            model.addAttribute("errorMessage", "検索条件を1つ以上入力してください");
            model.addAttribute("productSearchForm", form);
            return "product/ProductSearch";
        }

        // ★追加：0円未満チェック
        boolean invalidPrice =
                (form.getPurchasePriceMin() != null && form.getPurchasePriceMin() < 0) ||
                (form.getPurchasePriceMax() != null && form.getPurchasePriceMax() < 0) ||
                (form.getPriceMin() != null && form.getPriceMin() < 0) ||
                (form.getPriceMax() != null && form.getPriceMax() < 0);

        if (invalidPrice) {
            model.addAttribute("errorMessage", "価格は0円以上で入力してください");
            model.addAttribute("productSearchForm", form);
            return "product/ProductSearch";
        }

        // ★追加：範囲不正チェック
        boolean invalidRange =
                (form.getPurchasePriceMin() != null && form.getPurchasePriceMax() != null &&
                 form.getPurchasePriceMin() > form.getPurchasePriceMax()) ||

                (form.getPriceMin() != null && form.getPriceMax() != null &&
                 form.getPriceMin() > form.getPriceMax());

        if (invalidRange) {
            model.addAttribute("errorMessage", "最小値は最大値以下で入力してください");
            model.addAttribute("productSearchForm", form);
            return "product/ProductSearch";
        }

        List<ProductEntity> result = productService.search(form);

        if (result.isEmpty()) {
            model.addAttribute("errorMessage", "該当する商品は存在しません");
            model.addAttribute("productSearchForm", form);
            return "product/ProductSearch";
        }

        model.addAttribute("productList", result);

        // ★検索条件を結果画面へ渡す（戻る用）
        model.addAttribute("productSearchForm", form);

        return "product/ProductSearchConfirm";
    }

    // 戻る（これが重要）
    @PostMapping("/product/ProductSearchBack")
    public String productSearchBack(@ModelAttribute ProductSearchForm form,
                                    Model model) {

        model.addAttribute("productSearchForm", form);

        return "product/ProductSearch";
    }
}