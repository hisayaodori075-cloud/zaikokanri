package com.example.demo.controller;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserDetailsService userDetailsService;//あとで名前とgetterとかの処理変更//

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    
    @PostConstruct
    public void generateHash() {
        String rawPassword = "1234"; // nuup の新しいパスワード
        String hashed = passwordEncoder.encode(rawPassword);
        System.out.println(hashed);
    }
    
    public AuthController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
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
    
    // 新規登録 POST
    @PostMapping("/auth/register")
    public String registerUser(@RequestParam String username,
            					  @RequestParam String password) {

    		JdbcUserDetailsManager manager =
    				(JdbcUserDetailsManager) userDetailsService;

    		UserDetails newUser = User.builder()
    		        .username(username)
    		        .password(passwordEncoder.encode(password)) // ★ここが重要
    		        .roles("USER")
    		        .build();

    		manager.createUser(newUser);

    		return "redirect:/auth/login";
    }

    @GetMapping("/auth/top")
    public String topPage() {
        return "auth/top";
    }
}
