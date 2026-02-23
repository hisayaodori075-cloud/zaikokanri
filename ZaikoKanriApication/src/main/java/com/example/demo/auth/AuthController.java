package com.example.demo.auth;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    
    // ルート / へのアクセスでログイン画面にリダイレクト
    @GetMapping("/")
    public String root() {
        return "redirect:/auth/login";
    }

    // ログイン画面
    @GetMapping("/auth/login")
    public String loginForm() {
        return "/auth/login";
    }
    
    //ログイン処理
    @PostMapping("auth/login")
    public String login(@RequestParam String username,
                         @RequestParam String password,
                         HttpSession session) {

            // ★とりあえず固定値でテスト（まずはこれが超おすすめ）
            if (username.equals("test") && password.equals("1234")) {
                session.setAttribute("loginUser", username);
                return "/menu/ProductMasterApp";
            }
            return "auth/login";
        }
}
