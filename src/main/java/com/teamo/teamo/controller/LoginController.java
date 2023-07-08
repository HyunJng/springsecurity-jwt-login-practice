package com.teamo.teamo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "http://localhost:8080/oauth2/authorization/google";
    }
}
