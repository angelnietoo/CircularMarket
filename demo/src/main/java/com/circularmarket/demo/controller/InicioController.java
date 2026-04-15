package com.circularmarket.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/inicio")
    public String inicio(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("usuario", authentication.getName());
        } else {
            model.addAttribute("usuario", "Invitado");
        }
        return "inicio";
    }
}