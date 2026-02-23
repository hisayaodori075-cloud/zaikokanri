package com.example.demo.menu;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/menu")
@Controller
public class MenuController {
	// ログイン後画面
    @GetMapping("/ProductMasterApp")
    public String ProductMasterAppForm() {
        return "menu/ProductMasterApp";
    }
    
    @GetMapping("/ProductMasterKanri")
    public String ProductMasterKanriForm() {
        return "menu/ProductMasterKanri";
    }
    
    @GetMapping("/ProductMasterList")
    public String ProductMasterListForm(Model model) {
        model.addAttribute("ProductMasterList", new MenuProductMasterList());
        return "menu/ProductMasterList";
    }
}
