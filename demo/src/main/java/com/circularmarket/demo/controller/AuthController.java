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
        String nombre = registroRequest.getNombre() != null ? registroRequest.getNombre().trim() : "";
        String apellidos = registroRequest.getApellidos() != null ? registroRequest.getApellidos().trim() : "";
        String email = registroRequest.getEmail() != null ? registroRequest.getEmail().trim() : "";
        String password = registroRequest.getPassword() != null ? registroRequest.getPassword().trim() : "";
        String repetirPassword = registroRequest.getRepetirPassword() != null ? registroRequest.getRepetirPassword().trim() : "";

        if (nombre.isEmpty()) {
            model.addAttribute("error", "Debes introducir el nombre.");
            model.addAttribute("registroRequest", registroRequest);
            return "registro";
        }

        if (apellidos.isEmpty()) {
            model.addAttribute("error", "Debes introducir los apellidos.");
            model.addAttribute("registroRequest", registroRequest);
            return "registro";
        }

        if (email.isEmpty()) {
            model.addAttribute("error", "Debes introducir el email.");
            model.addAttribute("registroRequest", registroRequest);
            return "registro";
        }

        if (password.isEmpty()) {
            model.addAttribute("error", "Debes introducir la contraseña.");
            model.addAttribute("registroRequest", registroRequest);
            return "registro";
        }

        if (repetirPassword.isEmpty()) {
            model.addAttribute("error", "Debes repetir la contraseña.");
            model.addAttribute("registroRequest", registroRequest);
            return "registro";
        }

        if (!password.equals(repetirPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            model.addAttribute("registroRequest", registroRequest);
            return "registro";
        }

        if (password.length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
            model.addAttribute("registroRequest", registroRequest);
            return "registro";
        }

        try {
            usuarioService.registrar(registroRequest);
            return "redirect:/login?registrado=1";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("registroRequest", registroRequest);
            return "registro";
        } catch (Exception ex) {
            model.addAttribute("error", "Se ha producido un error al registrar al usuario.");
            model.addAttribute("registroRequest", registroRequest);
            return "registro";
        }
    }
}