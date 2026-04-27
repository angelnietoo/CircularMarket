package com.circularmarket.demo.controller;

import com.circularmarket.demo.model.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/inicio")
    public String inicio(Authentication authentication, Model model) {
        cargarUsuario(authentication, model);
        return "inicio";
    }

    @GetMapping("/carrito")
    public String carrito(Authentication authentication, Model model) {
        cargarUsuario(authentication, model);
        return "carrito";
    }

    @GetMapping("/configuracion")
    public String configuracion(Authentication authentication, Model model) {

        Usuario usuario = new Usuario();

        if (authentication != null) {
            usuario.setEmail(authentication.getName());
        }

        model.addAttribute("usuario", usuario);

        return "config";
    }

    private void cargarUsuario(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("usuarioNombre", authentication.getName());
        } else {
            model.addAttribute("usuarioNombre", "Invitado");
        }
    }
}