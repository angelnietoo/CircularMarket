package com.circularmarket.demo.config;

import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.UsuarioRepository;
import com.circularmarket.demo.service.CarritoService;
import com.circularmarket.demo.service.UsuarioAutenticado;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UsuarioViewAdvice {

    private final CarritoService carritoService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioViewAdvice(CarritoService carritoService,
                             UsuarioRepository usuarioRepository) {
        this.carritoService = carritoService;
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("usuarioNombre")
    public String usuarioNombre(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "Invitado";
        }

        Object principal = authentication.getPrincipal();

        if (principal == null || principal instanceof String) {
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

        if (principal instanceof OAuth2User oauth2User) {
            Object emailObject = oauth2User.getAttribute("email");

            if (emailObject != null && !emailObject.toString().isBlank()) {
                String email = emailObject.toString().trim().toLowerCase();

                return usuarioRepository.findByEmail(email)
                        .map(usuario -> {
                            String nombre = usuario.getNombre();

                            if (nombre != null && !nombre.isBlank()) {
                                return nombre.trim().split("\\s+")[0];
                            }

                            return usuario.getEmail();
                        })
                        .orElse(authentication.getName());
            }
        }

        return authentication.getName();
    }

    @ModelAttribute("carritoCantidad")
    public int carritoCantidad(Authentication authentication) {
        Long usuarioId = obtenerUsuarioIdAutenticado(authentication);

        if (usuarioId == null) {
            return 0;
        }

        return carritoService.contarItems(usuarioId);
    }

    private Long obtenerUsuarioIdAutenticado(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UsuarioAutenticado usuarioAutenticado) {
            return usuarioAutenticado.getId();
        }

        if (principal instanceof OAuth2User oauth2User) {
            Object emailObject = oauth2User.getAttribute("email");

            if (emailObject == null || emailObject.toString().isBlank()) {
                return null;
            }

            String email = emailObject.toString().trim().toLowerCase();

            return usuarioRepository.findByEmail(email)
                    .map(Usuario::getId)
                    .orElse(null);
        }

        String email = authentication.getName();

        if (email == null || email.isBlank()) {
            return null;
        }

        return usuarioRepository.findByEmail(email.trim().toLowerCase())
                .map(Usuario::getId)
                .orElse(null);
    }
}