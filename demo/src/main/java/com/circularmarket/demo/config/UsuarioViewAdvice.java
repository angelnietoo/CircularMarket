package com.circularmarket.demo.config;

import com.circularmarket.demo.service.UsuarioAutenticado;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UsuarioViewAdvice {

    @ModelAttribute("usuarioNombre")
    public String usuarioNombre(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "Invitado";
        }

        Object principal = authentication.getPrincipal();

        if (principal == null || principal instanceof String || principal instanceof AnonymousAuthenticationToken) {
            return "Invitado";
        }

        if (principal instanceof UsuarioAutenticado usuario) {
            String nombre = usuario.getNombre();

            if (nombre != null && !nombre.isBlank()) {
                return nombre.trim().split("\\s+")[0];
            }

            String nombreCompleto = usuario.getNombreCompleto();
            if (nombreCompleto != null && !nombreCompleto.isBlank()) {
                return nombreCompleto.trim().split("\\s+")[0];
            }

            if (usuario.getUsername() != null && !usuario.getUsername().isBlank()) {
                return usuario.getUsername();
            }
        }

        return authentication.getName();
    }
}