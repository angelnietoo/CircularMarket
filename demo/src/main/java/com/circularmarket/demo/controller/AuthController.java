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

    // Redirige la ruta principal hacia la pantalla de login.
    @GetMapping("/")
    public String raiz() {
        return "redirect:/login";
    }

    // Muestra la vista de inicio de sesión.
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Muestra el formulario de registro.
    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("registroRequest", new RegistroRequest());
        return "registro";
    }

    // Recibe los datos del formulario y registra un nuevo usuario.
    @PostMapping("/registro")
    public String registrar(@ModelAttribute RegistroRequest registroRequest, Model model) {

        // Comprueba que el nombre no esté vacío.
        if (registroRequest.getNombre() == null || registroRequest.getNombre().trim().isEmpty()) {
            model.addAttribute("error", "Debes introducir el nombre.");
            return "registro";
        }

        // Comprueba que los apellidos no estén vacíos.
        if (registroRequest.getApellidos() == null || registroRequest.getApellidos().trim().isEmpty()) {
            model.addAttribute("error", "Debes introducir los apellidos.");
            return "registro";
        }

        // Comprueba que el email no esté vacío.
        if (registroRequest.getEmail() == null || registroRequest.getEmail().trim().isEmpty()) {
            model.addAttribute("error", "Debes introducir el email.");
            return "registro";
        }

        // Comprueba que se haya introducido una contraseña.
        if (registroRequest.getPassword() == null || registroRequest.getPassword().isEmpty()) {
            model.addAttribute("error", "Debes introducir la contraseña.");
            return "registro";
        }

        // Comprueba que se haya repetido la contraseña.
        if (registroRequest.getRepetirPassword() == null || registroRequest.getRepetirPassword().isEmpty()) {
            model.addAttribute("error", "Debes repetir la contraseña.");
            return "registro";
        }

        // Comprueba que las dos contraseñas coincidan.
        if (!registroRequest.getPassword().equals(registroRequest.getRepetirPassword())) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registro";
        }

        // Comprueba que la contraseña tenga una longitud mínima.
        if (registroRequest.getPassword().length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
            return "registro";
        }

        // Comprueba que el usuario haya aceptado los términos.
        if (!registroRequest.isAceptaTerminos()) {
            model.addAttribute("error", "Debes aceptar los términos y condiciones de uso para registrarte.");
            return "registro";
        }

        try {
            // Si todo es correcto, registra el usuario desde el servicio.
            usuarioService.registrar(registroRequest);
            return "redirect:/login?registrado=1";

        } catch (IllegalArgumentException ex) {
            // Muestra errores controlados, como email ya registrado.
            model.addAttribute("error", ex.getMessage());
            return "registro";

        } catch (Exception ex) {
            // Controla cualquier error inesperado durante el registro.
            model.addAttribute("error", "Error al registrar el usuario.");
            return "registro";
        }
    }
}