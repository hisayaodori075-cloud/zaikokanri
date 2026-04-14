package com.example.demo.alert.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.alert.entity.AlertRotationSettingEntity;
import com.example.demo.alert.entity.AlertSettingEntity;
import com.example.demo.alert.service.AlertDisplayService;
import com.example.demo.alert.service.AlertRotationSettingService;
import com.example.demo.alert.service.AlertService;
import com.example.demo.alert.service.RotationAlertDisplayService;
import com.example.demo.product.entity.ProductEntity;
import com.example.demo.product.service.ProductService;

@Controller
@RequestMapping("/alert")
public class AlertController {

    @Autowired
    private AlertService alertService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private AlertRotationSettingService rotationService;
    
    @Autowired
    private AlertDisplayService alertDisplayService;
    
    @Autowired
    private RotationAlertDisplayService rotationAlertService;


    // 設定画面表示
    @GetMapping("/AlertScreen")
    public String menuAlert(Model model) {

        // 最低在庫アラート
        List<ProductEntity> minStockAlertList =
                alertDisplayService.findMinStockAlert();

        long minStockAlertCount =
                minStockAlertList != null ? minStockAlertList.size() : 0;

        // 回転緊急アラート
        long rotationUrgentCount =
                rotationAlertService.countUrgentAlert();

        model.addAttribute("minStockAlertCount", minStockAlertCount);
        model.addAttribute("rotationUrgentCount", rotationUrgentCount);

        return "alert/AlertScreen";
    }
    
    @GetMapping("/AlertSetting")
    public String alertSettingForm(Model model) {

        return "alert/AlertSetting";
    }
    
    @GetMapping("/AlertSettingList")
    public String alertSettingList(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String janCode,
            Model model) {

        // 商品名・JANコードで検索
        List<ProductEntity> productList = productService.findByJanOrName(janCode, productName);

        if (productList.isEmpty()) {
            model.addAttribute("errorMessage", "該当する商品は存在しません");
        }

        // 表示用リスト
        model.addAttribute("productList", productList);

        // プルダウン用リスト
        model.addAttribute("allProducts", productService.findAll());

        // 商品ID → AlertSettingEntity の Map を作成（未登録商品には Map に入らない）
        Map<Integer, AlertSettingEntity> alertMap = alertService.findAll().stream()
                .collect(Collectors.toMap(AlertSettingEntity::getProductId, e -> e));
        model.addAttribute("alertMap", alertMap);

        return "alert/AlertSettingList";
    }
    
 // 在庫最低数入力画面
    @GetMapping("/AlertSettingRegister/{id}")
    public String alertSettingRegister(@PathVariable Integer id, Model model) {
        // 商品情報取得
	    	ProductEntity product = productService.findByIdAndDeletedFalse(id);
	
	    	if (product == null) {
	    	    return "redirect:/alert/AlertSettingList";
	    	}

        // 既存のアラート設定を取得
        AlertSettingEntity alertSetting = alertService.findByProductId(product.getId());
        if (alertSetting == null) {
            // 新規の場合は空のエンティティを作成
            alertSetting = new AlertSettingEntity();
            alertSetting.setProductId(product.getId());
        }

        model.addAttribute("product", product);          // 商品情報
        model.addAttribute("alertSetting", alertSetting); // フォーム用エンティティ

        return "alert/AlertSettingRegister"; // Thymeleaf フォーム名
    }

    @PostMapping("AlertSettingConfirm")
    public String alertSettingConfirm(@ModelAttribute AlertSettingEntity alertSetting, Model model,
    													HttpSession session) {
	    	ProductEntity product =
	    	        productService.findByIdAndDeletedFalse(alertSetting.getProductId());
	
	    	if (product == null) {
	    	    return "redirect:/alert/AlertSettingList";
	    	}
        
        Integer minStock = alertSetting.getMinStock();
        if (minStock == null || minStock <= 0) {
            model.addAttribute("error", "最低在庫数は1以上で入力してください");
            model.addAttribute("product", product);
            model.addAttribute("alertSetting", alertSetting);
            return "alert/AlertSettingRegister";
        }
        
        model.addAttribute("product", product);
        model.addAttribute("alertSetting", alertSetting);
        session.setAttribute("alertConfirm", true);
        
        return "alert/AlertSettingConfirm"; // 確認画面テンプレート
    }

 // 完了画面（DB保存）
    @PostMapping("AlertSettingComplete")
    public String minStockComplete(
            @ModelAttribute AlertSettingEntity alertSetting,
            Model model,
            HttpSession session) {
    		
	    	Boolean flag = (Boolean) session.getAttribute("alertConfirm");
	
	    	if (flag == null || !flag) {
	    	    return "redirect:/alert/AlertSettingList";
	    	}

        // ★数量チェック（最終防衛）
        Integer minStock = alertSetting.getMinStock();
        if (minStock == null || minStock <= 0) {
            model.addAttribute("error", "最低在庫数は1以上で入力してください");

            ProductEntity product =
                    productService.findByIdAndDeletedFalse(alertSetting.getProductId());

            if (product == null) {
                return "redirect:/alert/AlertSettingList";
            }
            model.addAttribute("product", product);
            model.addAttribute("alertSetting", alertSetting);

            return "alert/AlertSettingRegister";
        }

        session.removeAttribute("alertConfirm");
        
        // 保存
        alertService.save(alertSetting);

        ProductEntity product = productService.findById(alertSetting.getProductId());
        model.addAttribute("productName", product != null ? product.getProductName() : "");

        return "alert/AlertSettingComplete";
    }
    
    @GetMapping("/AlertRotationSetting")
    public String rotationSettingForm(Model model) {

        AlertRotationSettingEntity setting = rotationService.getSetting();

        if (setting == null) {
            setting = new AlertRotationSettingEntity();
        }

        model.addAttribute("rotationSetting", setting);

        return "alert/AlertRotationSetting";
    }
    
    @PostMapping("/AlertRotationSettingConfirm")
    public String rotationSettingConfirm(
            @ModelAttribute AlertRotationSettingEntity rotationSetting,
            Model model,
            HttpSession session) {

        // ★日数チェック
        Integer urgentDays = rotationSetting.getUrgentDays();
        Integer attentionDays = rotationSetting.getAttentionDays();

        if (urgentDays == null || urgentDays <= 0 ||
            attentionDays == null || attentionDays <= 0) {

            model.addAttribute("error", "日数は1以上で入力してください");
            model.addAttribute("rotationSetting", rotationSetting);

            return "alert/AlertRotationSetting";
        }

        // ★数量チェック（追加）
        Integer urgentQty = rotationSetting.getUrgentSales();
        Integer attentionQty = rotationSetting.getAttentionSales();

        if (urgentQty == null || urgentQty <= 0 ||
            attentionQty == null || attentionQty <= 0) {

            model.addAttribute("error", "数量は1以上で入力してください");
            model.addAttribute("rotationSetting", rotationSetting);

            return "alert/AlertRotationSetting";
        }
        
        session.setAttribute("rotationConfirm", true);

        model.addAttribute("rotationSetting", rotationSetting);

        return "alert/AlertRotationSettingConfirm";
    }
    
    @PostMapping("/AlertRotationSettingComplete")
    public String rotationSettingComplete(
            @ModelAttribute AlertRotationSettingEntity rotationSetting,
            Model model,
            HttpSession session) {
    	
	    	Boolean flag = (Boolean) session.getAttribute("rotationConfirm");
	
	    	if (flag == null || !flag) {
	    	    return "redirect:/alert/AlertRotationSetting";
	    	}

        // ★日数チェック
        Integer urgentDays = rotationSetting.getUrgentDays();
        Integer attentionDays = rotationSetting.getAttentionDays();

        if (urgentDays == null || urgentDays <= 0 ||
            attentionDays == null || attentionDays <= 0) {

            model.addAttribute("error", "日数は1以上で入力してください");
            model.addAttribute("rotationSetting", rotationSetting);
            return "alert/AlertRotationSetting";
        }

        // ★数量チェック
        Integer urgentQty = rotationSetting.getUrgentSales();
        Integer attentionQty = rotationSetting.getAttentionSales();

        if (urgentQty == null || urgentQty <= 0 ||
            attentionQty == null || attentionQty <= 0) {

            model.addAttribute("error", "数量は1以上で入力してください");
            model.addAttribute("rotationSetting", rotationSetting);
            return "alert/AlertRotationSetting";
        }

        AlertRotationSettingEntity existing = rotationService.getSetting();

        if (existing == null) {

            // 初回登録（INSERT）
            rotationService.save(rotationSetting);

        } else {

            // 更新（UPDATE）
            rotationSetting.setId(existing.getId());
            rotationService.save(rotationSetting);
        }

        model.addAttribute("rotationSetting", rotationSetting);
        session.removeAttribute("rotationConfirm");

        return "alert/AlertRotationSettingComplete";
    }
    
    @GetMapping("/MinstockAlertDisplay")
    public String minstockAlertDisplay(Model model) {

        List<ProductEntity> minStockAlertList =
                alertDisplayService.findMinStockAlert();

        Map<Integer, AlertSettingEntity> alertMap =
                alertDisplayService.getAlertMap();

        model.addAttribute("minStockAlertList", minStockAlertList);
        model.addAttribute("alertMap", alertMap);

        return "alert/MinstockAlertDisplay";
    }
    
    @GetMapping("/RotationAlertDisplay")
    public String rotationAlertDisplay(Model model) {

        AlertRotationSettingEntity setting = rotationService.getSetting();

        List<ProductEntity> productList =
                rotationAlertService.findRotationAlert();

        Map<Integer, Integer> urgentSalesMap =
                rotationAlertService.getSalesCountMap(productList, setting.getUrgentDays());

        Map<Integer, Integer> attentionSalesMap =
                rotationAlertService.getSalesCountMap(productList, setting.getAttentionDays());

        Map<Integer, String> lastSalesDateMap =
                rotationAlertService.getLastSalesDateMap(productList);

        model.addAttribute("rotationAlertList", productList);
        model.addAttribute("urgentSalesMap", urgentSalesMap);
        model.addAttribute("attentionSalesMap", attentionSalesMap);
        model.addAttribute("lastSalesDateMap", lastSalesDateMap);
        model.addAttribute("setting", setting);

        return "alert/RotationAlertDisplay";
    }
}