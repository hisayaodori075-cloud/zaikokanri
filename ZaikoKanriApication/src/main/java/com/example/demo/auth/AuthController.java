package com.example.demo.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // ルート
    @GetMapping("/")
    public String root() {
        return "redirect:/auth/login";
    }

    // ログイン画面表示だけ
    @GetMapping("/auth/login")
    public String loginForm() {
        return "auth/login";
    }
    
    @GetMapping("/auth/register")
    public String registerForm() {
        return "auth/register";
    }
    

    // 登録処理
    @PostMapping("/auth/register")
    public String register(@RequestParam String username,
                           @RequestParam String password) {

        userService.register(username, password);

        return "redirect:/auth/login";
    }
}