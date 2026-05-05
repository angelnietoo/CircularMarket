package com.circularmarket.demo.controller;

import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.RolUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private RolUsuarioRepository rolUsuarioRepository;

    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolUsuarioRepository.findAll());
        return "admin/usuarios-formulario";
    }
}