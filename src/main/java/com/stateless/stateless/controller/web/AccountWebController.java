package com.stateless.stateless.controller.web;

import com.stateless.stateless.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class AccountWebController {

    @GetMapping
    public String index(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "account/index"; // Esta es la página del menú
    }
}