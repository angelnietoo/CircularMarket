package com.circularmarket.demo.controller;

import com.circularmarket.demo.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // Muestra el formulario para solicitar la recuperación de contraseña.
    @GetMapping("/recuperar-contrasena")
    public String mostrarFormularioOlvidoPassword() {
        return "recuperar-contrasena";
    }

    // Recibe el email y solicita el envío del enlace de recuperación.
    @PostMapping("/recuperar-contrasena")
    public String procesarFormularioOlvidoPassword(@RequestParam String email, Model model) {

        try {
            // Genera el token y envía el correo de recuperación.
            passwordResetService.solicitarRestablecimiento(email);

            // Muestra un mensaje genérico para no revelar si el correo existe o no.
            model.addAttribute(
                    "mensaje",
                    "Si existe una cuenta con ese correo, recibirás un enlace para restablecer la contraseña."
            );

        } catch (Exception ex) {
            // Muestra error si falla el envío del correo.
            model.addAttribute(
                    "error",
                    "No se ha podido enviar el correo de recuperación. Revisa la configuración del correo."
            );
        }

        return "recuperar-contrasena";
    }

    // Muestra el formulario para escribir la nueva contraseña.
    @GetMapping("/nueva-contrasena")
    public String mostrarFormularioNuevaPassword(@RequestParam String token, Model model) {

        try {
            // Comprueba que el token existe y no ha caducado.
            passwordResetService.obtenerUsuarioPorTokenValido(token);

            model.addAttribute("token", token);
            return "nueva-contrasena";

        } catch (IllegalArgumentException ex) {
            // Si el token no es válido, muestra el error en la misma vista.
            model.addAttribute("error", ex.getMessage());
            return "nueva-contrasena";
        }
    }

    // Guarda la nueva contraseña usando el token recibido.
    @PostMapping("/nueva-contrasena")
    public String procesarNuevaPassword(@RequestParam String token,
                                        @RequestParam String password,
                                        @RequestParam String repetirPassword,
                                        Model model) {

        try {
            // Valida el token y actualiza la contraseña del usuario.
            passwordResetService.restablecerContrasena(token, password, repetirPassword);

            return "redirect:/login?passwordReset=1";

        } catch (IllegalArgumentException ex) {
            // Muestra errores controlados, como token inválido o contraseñas distintas.
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("token", token);
            return "nueva-contrasena";

        } catch (Exception ex) {
            // Controla cualquier error inesperado al cambiar la contraseña.
            model.addAttribute("error", "No se ha podido restablecer la contraseña.");
            model.addAttribute("token", token);
            return "nueva-contrasena";
        }
    }
}