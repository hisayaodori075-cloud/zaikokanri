package com.example.demo.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.menu.MenuProductRepository;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private MenuProductRepository productRepository;

    // 新規登録画面表示
    @GetMapping("/newproduct")
    public String showForm(Model model) {
        model.addAttribute("product", new ProductModel());
        return "product/newproduct";
    }
    

    // 確認画面へ
    @PostMapping("/confirm")
    public String confirm(@ModelAttribute ProductModel product, Model model) {
        model.addAttribute("product", product);
        return "product/confirm";
    }

    // 登録処理
    @PostMapping("/save")
    public String save(@ModelAttribute ProductModel product, Model model) {

        // 🔽 Model → Entity に変換
        ProductEntity entity = new ProductEntity();
        entity.setJanCode(product.getJan());
        entity.setMakerName(product.getMakerName());
        entity.setProductName(product.getName());
        entity.setPrice(product.getPrice());
        entity.setStatus(product.getStatus());
        entity.setSalesStatus(product.getSalesStatus());

        // 🔽 DB保存
        productRepository.save(entity);

        model.addAttribute("product", product);
        return "product/complete";
    }
}