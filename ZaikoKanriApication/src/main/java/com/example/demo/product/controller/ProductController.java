package com.example.demo.product.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

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
    
    @GetMapping("/confirm")
    public String confirmGet() {
        return "redirect:/product/newproduct";
    }

    @PostMapping("/confirm")
    public String confirm(@ModelAttribute ProductEntity product, 
    		  								 Model model,
    		  								 HttpSession session) {

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
        
        // JAN重複チェック
        if (productService.isJanCodeDuplicate(product.getJanCode())) {
            model.addAttribute("error", "このJANコードは既に登録されています");
            model.addAttribute("product", product);
            return "product/newproduct";
        }
        
        if (product.getMakerName() != null && product.getMakerName().length() > 30) {
            model.addAttribute("error", "メーカー名は30文字以内で入力してください");
            model.addAttribute("product", product);
            return "product/newproduct";
        }

        if (product.getProductName() != null && product.getProductName().length() > 30) {
            model.addAttribute("error", "商品名は30文字以内で入力してください");
            model.addAttribute("product", product);
            return "product/newproduct";
        }
        
        session.setAttribute("productConfirm", true);

        model.addAttribute("product", product);
        return "product/confirm";
    }

    @GetMapping("/save")
    public String saveGet() {
        return "redirect:/product/newproduct";
    }
    
    @PostMapping("/save")
    public String save(@ModelAttribute ProductEntity product, 
    									  Model model,
    									  HttpSession session) {
	    	
    		Boolean flag = (Boolean) session.getAttribute("productConfirm");
	
	    	if (flag == null || !flag) {
	    	    return "redirect:/product/newproduct";
	    	}

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
        
        // JAN重複チェック
        if (productService.isJanCodeDuplicate(product.getJanCode())) {
            model.addAttribute("error", "このJANコードは既に登録されています");
            model.addAttribute("product", product);
            return "product/newproduct";
        }
        
     // ★文字数チェック
        if (product.getMakerName() != null && product.getMakerName().length() > 30) {
            model.addAttribute("error", "メーカー名は30文字以内で入力してください");
            model.addAttribute("product", product);
            return "product/newproduct";
        }

        if (product.getProductName() != null && product.getProductName().length() > 30) {
            model.addAttribute("error", "商品名は30文字以内で入力してください");
            model.addAttribute("product", product);
            return "product/newproduct";
        }
        
        session.removeAttribute("productConfirm");

        productService.save(product);
        model.addAttribute("product", product);
        
        return "product/complete";
    }

    // ---------------- 編集 ----------------
    @GetMapping("/ProductEdit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
    	ProductEntity product = productService.findByIdAndDeletedFalse(id);

	    	if (product == null) {
	    	    return "redirect:/product/ProductMasterList";
	    	}
	        model.addAttribute("product", product);
	        return "product/ProductEdit";
	    }

    @PostMapping("/ProductEdit/{id}")
    public String editSubmit(@ModelAttribute ProductEntity product,
                             Model model,
                             HttpSession session) {

        // ★バリデーション
        if (product.getPurchasePrice() != null && product.getPurchasePrice() < 0) {
            model.addAttribute("error", "仕入価格にマイナスは入力できません");
            model.addAttribute("product", product);
            return "product/ProductEdit";
        }

        if (product.getPrice() != null && product.getPrice() < 0) {
            model.addAttribute("error", "売価にマイナスは入力できません");
            model.addAttribute("product", product);
            return "product/ProductEdit";
        }

        if (product.getJanCode() != null &&
                product.getJanCode().contains("-")) {

            model.addAttribute("error", "JANコードにマイナスは使用できません");
            model.addAttribute("product", product);
            return "product/ProductEdit";
        }

        // JAN重複チェック
        if (productService.isJanCodeDuplicateForUpdate(
                product.getJanCode(), product.getId())) {

            model.addAttribute("error", "このJANコードは既に登録されています");
            model.addAttribute("product", product);
            return "product/ProductEdit";
        }
        
     // ★文字数チェック
        if (product.getMakerName() != null && product.getMakerName().length() > 30) {
            model.addAttribute("error", "メーカー名は30文字以内で入力してください");
            model.addAttribute("product", product);
            return "product/ProductEdit";
        }

        if (product.getProductName() != null && product.getProductName().length() > 30) {
            model.addAttribute("error", "商品名は30文字以内で入力してください");
            model.addAttribute("product", product);
            return "product/ProductEdit";
        }

        // ★ここが追加（廃棄と同じ思想）
        session.setAttribute("productEditConfirm", true);

        model.addAttribute("product", product);
        return "product/ProductEditConfirm";
    }
    
    @GetMapping("/ProductEditComplete")
    public String editCompleteGet() {
        return "redirect:/product/ProductMasterList";
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
                return "product/ProductEdit";
            }
        
        // JAN重複チェック
        if (productService.isJanCodeDuplicateForUpdate(
                product.getJanCode(), product.getId())) {

            model.addAttribute("error", "このJANコードは既に登録されています");
            model.addAttribute("product", product);
            return "product/ProductEdit";
        }
        
        // ★文字数チェック
        if (product.getMakerName() != null && product.getMakerName().length() > 30) {
            model.addAttribute("error", "メーカー名は30文字以内で入力してください");
            model.addAttribute("product", product);
            return "product/ProductEdit";
        }

        if (product.getProductName() != null && product.getProductName().length() > 30) {
            model.addAttribute("error", "商品名は30文字以内で入力してください");
            model.addAttribute("product", product);
            return "product/ProductEdit";
        }

        // DBから元データ取得
        ProductEntity dbProduct =
        	    productService.findByIdAndDeletedFalse(product.getId());

        	if (dbProduct == null) {
        	    return "redirect:/product/ProductMasterList";
        	}

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
    	ProductEntity product = productService.findByIdAndDeletedFalse(id);

	    	if (product == null) {
	    	    return "redirect:/product/ProductMasterList";
	    	}
        model.addAttribute("product", product);
        return "product/ProductDelete";
    }

    @GetMapping("/ProductDeleteComplete/{id}")
    public String deleteCompleteGet(@PathVariable Integer id) {
        return "redirect:/product/ProductMasterList";
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