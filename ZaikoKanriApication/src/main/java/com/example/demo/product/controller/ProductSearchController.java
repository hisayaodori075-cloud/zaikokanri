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

        List<ProductEntity> result = productService.search(form);
        model.addAttribute("productList", result);

        return "product/ProductSearchConfirm";
    }
}