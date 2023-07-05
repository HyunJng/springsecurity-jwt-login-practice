package com.teamo.teamo.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class LoginController {

    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model) {
        return "main페이지";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login페이지";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        return "admin페이지";
    }
}
