package com.circularmarket.demo.service;

import com.circularmarket.demo.model.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service("usuarioSesion")
public class UsuarioSesionService {

    private final UsuarioService usuarioService;

    public UsuarioSesionService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public String obtenerNombre(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "Usuario";
        }

        String email = obtenerEmail(authentication);

        if (email != null && !email.isBlank()) {
            Usuario usuario = usuarioService.buscarPorEmail(email);

            if (usuario != null && usuario.getNombre() != null && !usuario.getNombre().isBlank()) {
                return usuario.getNombre();
            }
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UsuarioAutenticado usuarioAutenticado) {
            String nombre = usuarioAutenticado.getNombre();

            if (nombre != null && !nombre.isBlank()) {
                return nombre;
            }
        }

        if (principal instanceof OAuth2User oauth2User) {
            Object givenName = oauth2User.getAttributes().get("given_name");

            if (givenName != null && !givenName.toString().isBlank()) {
                return givenName.toString();
            }

            Object name = oauth2User.getAttributes().get("name");

            if (name != null && !name.toString().isBlank()) {
                return name.toString();
            }
        }

        return "Usuario";
    }

    public String obtenerEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UsuarioAutenticado usuarioAutenticado) {
            return usuarioAutenticado.getUsername();
        }

        if (principal instanceof OAuth2User oauth2User) {
            Object email = oauth2User.getAttributes().get("email");

            if (email != null) {
                return email.toString().trim().toLowerCase();
            }
        }

        String name = authentication.getName();

        if (name != null && name.contains("@")) {
            return name.trim().toLowerCase();
        }

        return null;
    }
}