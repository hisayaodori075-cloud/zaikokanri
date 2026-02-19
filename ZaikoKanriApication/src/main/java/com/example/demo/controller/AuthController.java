package com.example.demo.controller;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final InMemoryUserDetailsManager userDetailsManager;

    public AuthController(InMemoryUserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }
    
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

    // 新規登録画面
    @GetMapping("/auth/register")
    public String registerForm() {
        return "/auth/register";
    }
    
    @GetMapping("/menu/FirstMenu")
    public String FirstMenuForm() {
        return "/menu/FirstMenu";
    }
    
    // 新規登録 POST
    @PostMapping("/auth/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        UserDetails newUser = User.withDefaultPasswordEncoder()
                .username(username)
                .password(password)
                .roles("USER")
                .build();
        userDetailsManager.createUser(newUser);
        return "redirect:/auth/login";	// 登録後にログイン画面へ
    }

    @GetMapping("/auth/top")
    public String topPage() {
        return "auth/top";
    }
}
