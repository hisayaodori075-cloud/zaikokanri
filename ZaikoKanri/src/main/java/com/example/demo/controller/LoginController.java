package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.form.LoginForm;

import ch.qos.logback.core.model.Model;

@Controller
public class LoginController {
	@GetMapping("/login")
	public String view(Model model, LoginForm form) {
		
		return "login";
	}
}
