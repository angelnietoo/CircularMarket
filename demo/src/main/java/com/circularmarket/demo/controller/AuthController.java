package com.circularmarket.demo.controller;

import com.circularmarket.demo.dto.RegistroRequest;
import com.circularmarket.demo.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("registroRequest", new RegistroRequest());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute RegistroRequest registroRequest, Model model) {

        //  Validación básica de los campos del formulario
        if (registroRequest.getNombre() == null || registroRequest.getNombre().trim().isEmpty()) {
            model.addAttribute("error", "Debes introducir el nombre.");
            return "registro";
        }

        if (registroRequest.getApellidos() == null || registroRequest.getApellidos().trim().isEmpty()) {
            model.addAttribute("error", "Debes introducir los apellidos.");
            return "registro";
        }

        if (registroRequest.getEmail() == null || registroRequest.getEmail().trim().isEmpty()) {
            model.addAttribute("error", "Debes introducir el email.");
            return "registro";
        }

        if (registroRequest.getPassword() == null || registroRequest.getPassword().isEmpty()) {
            model.addAttribute("error", "Debes introducir la contraseña.");
            return "registro";
        }

        if (registroRequest.getRepetirPassword() == null || registroRequest.getRepetirPassword().isEmpty()) {
            model.addAttribute("error", "Debes repetir la contraseña.");
            return "registro";
        }

        if (!registroRequest.getPassword().equals(registroRequest.getRepetirPassword())) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registro";
        }

        if (registroRequest.getPassword().length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
            return "registro";
        }

        try {
            usuarioService.registrar(registroRequest);
            return "redirect:/login?registrado=1";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "registro";

        } catch (Exception ex) {
            model.addAttribute("error", "Error al registrar el usuario.");
            return "registro";
        }
    }
}