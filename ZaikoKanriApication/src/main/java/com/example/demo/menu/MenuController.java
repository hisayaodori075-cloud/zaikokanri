package com.example.demo.menu;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.alert.service.AlertDisplayService;
import com.example.demo.alert.service.RotationAlertDisplayService;
import com.example.demo.product.entity.ProductEntity;

@RequestMapping("/menu")
@Controller
public class MenuController {

    @Autowired
    private AlertDisplayService alertDisplayService;

    @Autowired
    private RotationAlertDisplayService rotationAlertService;

    // ログイン後画面
    @GetMapping("/ProductMasterApp")
    public String productMasterApp(Model model) {

        // 最低在庫アラート
        List<ProductEntity> minStockAlertList =
                alertDisplayService.findMinStockAlert();

        long minStockAlertCount =
                minStockAlertList != null ? minStockAlertList.size() : 0;

        // 回転アラート
        long rotationUrgentCount =
                rotationAlertService.countUrgentAlert();

        // 合計
        long totalAlertCount =
                minStockAlertCount + rotationUrgentCount;

        model.addAttribute("totalAlertCount", totalAlertCount);

        return "menu/ProductMasterApp";
    }

    @GetMapping("/ProductMasterKanri")
    public String ProductMasterKanriForm() {
        return "menu/ProductMasterKanri";
    }

}