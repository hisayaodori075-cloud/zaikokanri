package com.example.demo.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String registerForm(Model model) {

        model.addAttribute("user", new UserEntity());

        return "auth/register";
    }
    
    @GetMapping("/auth/confirm")
    public String confirmGet() {
        return "redirect:/auth/register";
    }

    @GetMapping("/auth/complete")
    public String completeGet() {
        return "redirect:/auth/register";
    }
    

    // 登録処理
    @PostMapping("/auth/register")
    public String registerConfirm(@ModelAttribute UserEntity user,
                                  Model model) {

        // ユーザー名未入力
        if (user.getUsername() == null ||
            user.getUsername().isBlank()) {

            model.addAttribute("errorMessage",
                    "ユーザー名を入力してください");

            model.addAttribute("user", user);

            return "auth/register";
        }

        // パスワード未入力
        if (user.getPassword() == null ||
            user.getPassword().isBlank()) {

            model.addAttribute("errorMessage",
                    "パスワードを入力してください");

            model.addAttribute("user", user);

            return "auth/register";
        }

        // 重複チェック
        if (userService.existsByUsername(user.getUsername())) {

            model.addAttribute("errorMessage",
                    "そのユーザー名は既に使用されています");

            model.addAttribute("user", user);

            return "auth/register";
        }

        model.addAttribute("user", user);

        return "auth/confirm";
    }
    
    @PostMapping("/auth/complete")
    public String registerComplete(@ModelAttribute UserEntity user) {

        userService.register(
                user.getUsername(),
                user.getPassword());

        return "auth/complete";
    }
    
    @PostMapping("/auth/registerBack")
    public String registerBack(@ModelAttribute UserEntity user,
                               Model model) {

        model.addAttribute("user", user);

        return "auth/register";
    }
}