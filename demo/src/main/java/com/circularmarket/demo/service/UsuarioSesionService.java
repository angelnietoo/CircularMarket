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

    // Obtiene el nombre del usuario que tiene la sesión iniciada.
    public String obtenerNombre(Authentication authentication) {
        // Si no hay usuario autenticado, devuelve un nombre genérico.
        if (authentication == null || !authentication.isAuthenticated()) {
            return "Usuario";
        }

        // Primero intenta obtener el email del usuario autenticado.
        String email = obtenerEmail(authentication);

        if (email != null && !email.isBlank()) {
            Usuario usuario = usuarioService.buscarPorEmail(email);

            // Si encuentra el usuario en la base de datos, devuelve su nombre.
            if (usuario != null && usuario.getNombre() != null && !usuario.getNombre().isBlank()) {
                return usuario.getNombre();
            }
        }

        Object principal = authentication.getPrincipal();

        // Caso de login normal con usuario propio de la aplicación.
        if (principal instanceof UsuarioAutenticado usuarioAutenticado) {
            String nombre = usuarioAutenticado.getNombre();

            if (nombre != null && !nombre.isBlank()) {
                return nombre;
            }
        }

        // Caso de login con Google OAuth2.
        if (principal instanceof OAuth2User oauth2User) {
            Object givenName = oauth2User.getAttributes().get("given_name");

            // Intenta obtener el nombre que devuelve Google.
            if (givenName != null && !givenName.toString().isBlank()) {
                return givenName.toString();
            }

            Object name = oauth2User.getAttributes().get("name");

            // Si no hay given_name, usa el nombre completo de Google.
            if (name != null && !name.toString().isBlank()) {
                return name.toString();
            }
        }

        // Si no se puede obtener ningún nombre, devuelve uno genérico.
        return "Usuario";
    }

    // Obtiene el email del usuario autenticado.
    public String obtenerEmail(Authentication authentication) {
        // Si no hay sesión iniciada, no devuelve email.
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // Caso de login normal con usuario propio de la aplicación.
        if (principal instanceof UsuarioAutenticado usuarioAutenticado) {
            return usuarioAutenticado.getUsername();
        }

        // Caso de login con Google OAuth2.
        if (principal instanceof OAuth2User oauth2User) {
            Object email = oauth2User.getAttributes().get("email");

            if (email != null) {
                return email.toString().trim().toLowerCase();
            }
        }

        // Caso alternativo usando el nombre guardado por Spring Security.
        String name = authentication.getName();

        if (name != null && name.contains("@")) {
            return name.trim().toLowerCase();
        }

        return null;
    }
}