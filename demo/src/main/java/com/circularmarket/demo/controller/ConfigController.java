package com.circularmarket.demo.controller;

import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ConfigController {

    private final UsuarioService usuarioService;

    public ConfigController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/configuracion")
    public String configuracion(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Usuario usuarioAutenticado = usuarioService.buscarPorEmail(email);

        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.buscarPorId(usuarioAutenticado.getId());

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "config";
    }

    @PostMapping("/configuracion")
    public String guardarConfiguracion(
            Usuario usuarioForm,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String repetirPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String emailActual = authentication.getName();
        Usuario usuarioAutenticado = usuarioService.buscarPorEmail(emailActual);

        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }

        try {
            usuarioService.actualizarUsuario(
                    usuarioAutenticado.getId(),
                    usuarioForm,
                    password,
                    repetirPassword
            );

            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/configuracion";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuarioForm);
            return "config";
        }
    }
}