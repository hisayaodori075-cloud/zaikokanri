package com.example.demo.stock;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/alert")
public class AlertController {
	
	// アラート画面へ
	@GetMapping("/AlertScreen")
    public String AlertScreenForm() {
        return "alert/AlertScreen";
    }
	
	
	@GetMapping("/AlertSetting")
    public String AlertSettingForm() {
        return "alert/AlertSetting";
    }

}
