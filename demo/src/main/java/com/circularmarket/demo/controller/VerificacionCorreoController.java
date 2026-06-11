package com.circularmarket.demo.controller;

import com.circularmarket.demo.service.VerificacionCorreoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VerificacionCorreoController {

    private final VerificacionCorreoService verificacionCorreoService;

    public VerificacionCorreoController(VerificacionCorreoService verificacionCorreoService) {
        this.verificacionCorreoService = verificacionCorreoService;
    }

    // Verifica el correo cuando el usuario pulsa el enlace recibido por email.
    @GetMapping("/verificar-correo")
    public String verificarCorreo(@RequestParam String token, Model model) {
        try {
            // Comprueba el token y marca el correo como verificado.
            verificacionCorreoService.verificarCorreo(token);

            // Redirige al login mostrando un mensaje de verificación correcta.
            return "redirect:/login?emailVerificado=1";

        } catch (IllegalArgumentException ex) {
            // Muestra una vista de error si el enlace no es válido o ha caducado.
            model.addAttribute("error", ex.getMessage());
            return "verificar-correo-error";
        }
    }
}