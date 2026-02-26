package com.example.demo.form.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.form.ProductSearchForm;
import com.example.demo.product.ProductEntity;

@Controller
public class ProductSearchController {

    @Autowired
    private ProductService productService;

    @GetMapping("/inputForm/ProductSearch")
    public String showSearchForm(Model model) {
        model.addAttribute("productSearchForm", new ProductSearchForm());
        return "productSearch";
    }

    @PostMapping("/inputForm/ProductSearch")
    public String search(
            @ModelAttribute ProductSearchForm form,
            Model model) {

        List<ProductEntity> result = productService.search(form);
        model.addAttribute("productList", result);

        return "productList";
    }
}