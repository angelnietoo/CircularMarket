package com.circularmarket.demo.controller;

import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.service.UsuarioAutenticado;
import com.circularmarket.demo.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    // Muestra la pantalla de configuración del usuario autenticado.
    @GetMapping("/configuracion")
    public String configuracion(Model model, Authentication authentication) {
        // Si no hay sesión iniciada, redirige al login.
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Obtiene el email según el tipo de login usado.
        String email = obtenerEmailAutenticado(authentication);

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        // Busca el usuario usando el email autenticado.
        Usuario usuarioAutenticado = usuarioService.buscarPorEmail(email);

        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }

        // Recarga el usuario por ID para obtener sus datos actualizados.
        Usuario usuario = usuarioService.buscarPorId(usuarioAutenticado.getId());

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "config";
    }

    // Guarda los cambios de configuración del usuario.
    @PostMapping("/configuracion")
    public String guardarConfiguracion(
            Usuario usuarioForm,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String repetirPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Comprueba que el usuario siga autenticado antes de guardar cambios.
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Obtiene el email actual de la sesión.
        String emailActual = obtenerEmailAutenticado(authentication);

        if (emailActual == null || emailActual.isBlank()) {
            return "redirect:/login";
        }

        // Busca el usuario real que está modificando sus datos.
        Usuario usuarioAutenticado = usuarioService.buscarPorEmail(emailActual);

        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }

        try {
            // Actualiza los datos del usuario.
            usuarioService.actualizarUsuario(
                    usuarioAutenticado.getId(),
                    usuarioForm,
                    password,
                    repetirPassword
            );

            // Muestra mensaje de éxito tras redirigir.
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/configuracion";

        } catch (IllegalArgumentException e) {
            // Si hay errores de validación, vuelve al formulario con el mensaje.
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuarioForm);
            return "config";
        }
    }

    // Obtiene el email del usuario según el tipo de autenticación.
    private String obtenerEmailAutenticado(Authentication authentication) {
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

        // Caso alternativo usando el nombre de la autenticación.
        String name = authentication.getName();

        if (name != null && name.contains("@")) {
            return name.trim().toLowerCase();
        }

        return null;
    }
}