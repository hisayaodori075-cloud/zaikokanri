package com.example.demo.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sales")
public class SalesController {
	
	// 販売数入力
	@GetMapping("/SalesManegement")
    public String SalesManegementForm() {
        return "sales/SalesManegement";
    }

}
