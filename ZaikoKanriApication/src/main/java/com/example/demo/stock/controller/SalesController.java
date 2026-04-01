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

    // ---------------- 販売数入力 ----------------
    @GetMapping("/SalesRegister")
    public String salesRegisterForm(Model model) {

        model.addAttribute("sales", new SalesEntity());

        List<ProductEntity> productList = productService.findAll();
        model.addAttribute("productList", productList);

        return "sales/SalesRegister";
    }
    
 // ---------------- 販売履歴一覧 ----------------
    @GetMapping("/SalesList")
    public String salesList(Model model) {

        List<SalesEntity> salesList = salesService.getSalesList();
        List<ProductEntity> productList = productService.findAll();

        model.addAttribute("salesList", salesList);
        model.addAttribute("productList", productList);

        return "sales/SalesList";
    }

    @PostMapping("/SalesRegister")
    public String search(
            @ModelAttribute SalesEntity sales,
            @RequestParam(required = false) String janCode,
            Model model) {

        ProductEntity product = null;

        if (janCode != null && !janCode.isBlank()) {

            product = productService.findByJanCode(janCode.trim());

            if (product == null) {

                model.addAttribute("errorMessage", "JANコードが存在しません");

                model.addAttribute("productList", productService.findAll());
                model.addAttribute("sales", sales);

                return "sales/SalesRegister";
            }

            if (sales.getProductId() != null) {

                if (!product.getId().equals(sales.getProductId())) {

                    model.addAttribute("errorMessage", "JANコードと商品が一致しません");

                    model.addAttribute("productList", productService.findAll());
                    model.addAttribute("sales", sales);

                    return "sales/SalesRegister";
                }
            }

        } else if (sales.getProductId() != null) {

            product = productService.findByIdAndDeletedFalse(sales.getProductId());

            if (product == null) {

                model.addAttribute("errorMessage", "商品が存在しません");

                model.addAttribute("productList", productService.findAll());
                model.addAttribute("sales", sales);

                return "sales/SalesRegister";
            }
        }

        if (product == null) {

            model.addAttribute("errorMessage", "商品を指定してください");

            model.addAttribute("productList", productService.findAll());
            model.addAttribute("sales", sales);

            return "sales/SalesRegister";
        }

        sales.setProductId(product.getId());

        model.addAttribute("product", product);
        model.addAttribute("sales", sales);

        return "sales/SalesConfirm";
    }

    // ---------------- 販売確認 ----------------
    @PostMapping("/SalesConfirm")
    public String salesConfirm(@ModelAttribute SalesEntity sales, Model model) {

        ProductEntity product = productService.findByIdAndDeletedFalse(sales.getProductId());

        if (product == null) {

            model.addAttribute("errorMessage", "商品が存在しません");

            return "sales/SalesManagement";
        }

        model.addAttribute("sales", sales);
        model.addAttribute("product", product);

        return "sales/SalesConfirm";
    }

    @PostMapping("/SalesConfirm2")
    public String confirm(@ModelAttribute SalesEntity sales, Model model) {

        ProductEntity product = productService.findByIdAndDeletedFalse(sales.getProductId());

        if (product == null) {

            model.addAttribute("errorMessage", "商品が見つかりません");

            return "sales/SalesManagement";
        }

        model.addAttribute("sales", sales);
        model.addAttribute("product", product);

        return "sales/SalesConfirm2";
    }

    // ---------------- 保存 ----------------
    @PostMapping("/SalesSave")
    public String salesSave(@ModelAttribute SalesEntity sales, Model model) {

        ProductEntity product = productService.findByIdAndDeletedFalse(sales.getProductId());

        if (product == null) {

            model.addAttribute("errorMessage", "商品が存在しません");

            return "sales/SalesManagement";
        }

        if (sales.getSalesQuantity() == null) {

            model.addAttribute("errorMessage", "販売数を入力してください");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);

            return "sales/SalesConfirm";
        }

        if (product.getStock() < sales.getSalesQuantity()) {

            model.addAttribute("errorMessage", "在庫が不足しています");
            model.addAttribute("sales", sales);
            model.addAttribute("product", product);

            return "sales/SalesConfirm";
        }

        salesService.save(sales);

        return "sales/SalesComplete";
    }

    // ---------------- 販売修正 ----------------
    @GetMapping("/SalesEditSearch")
    public String salesEditSearchForm(Model model) {

        return "sales/SalesEditSearch";
    }

    @PostMapping("/SalesEdit")
    public String salesInEdit(@RequestParam Integer id, Model model) {

        SalesEntity sales = salesService.findById(id);

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

        ProductEntity product = productService.findByIdAndDeletedFalse(sales.getProductId());

        if (product == null) {

            model.addAttribute("errorMessage", "商品が存在しません");

            return "sales/SalesEditSearch";
        }

        model.addAttribute("sales", sales);
        model.addAttribute("product", product);

        return "sales/SalesEditConfirm";
    }

    // ---------------- 削除 ----------------
    @GetMapping("/SalesDeleteSearch")
    public String salesDeleteSearchForm(Model model) {

        return "sales/SalesDeleteSearch";
    }

    @PostMapping("/SalesDeleteSearch")
    public String salesDeleteConfirm(@RequestParam Integer id, Model model) {

        SalesEntity sales = salesService.findById(id);

        if (sales == null || sales.isDeleted()) {

            model.addAttribute("errorMessage", "そのIDの販売数データは存在しません");

            return "sales/SalesDeleteSearch";
        }

        ProductEntity product = productService.findByIdAndDeletedFalse(sales.getProductId());

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

        salesService.delete(id);

        return "sales/SalesDeleteComplete";
    }

    @PostMapping("/SalesEditSave")
    public String salesEditSave(@ModelAttribute SalesEntity sales) {

        salesService.update(sales);

        return "sales/SalesEditComplete";
    }
}