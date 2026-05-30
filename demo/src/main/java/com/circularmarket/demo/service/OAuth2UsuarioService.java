package com.circularmarket.demo.service;

import com.circularmarket.demo.model.RolUsuario;
import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.RolUsuarioRepository;
import com.circularmarket.demo.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class OAuth2UsuarioService implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public OAuth2UsuarioService(UsuarioRepository usuarioRepository,
                                RolUsuarioRepository rolUsuarioRepository,
                                PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof OAuth2User oauth2User)) {
            response.sendRedirect(request.getContextPath() + "/login?error=google");
            return;
        }

        Map<String, Object> atributos = oauth2User.getAttributes();

        String email = obtenerTexto(atributos, "email");
        String nombre = obtenerTexto(atributos, "given_name");
        String apellidos = obtenerTexto(atributos, "family_name");
        String nombreCompleto = obtenerTexto(atributos, "name");

        if (email == null || email.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/login?error=google-email");
            return;
        }

        email = email.trim().toLowerCase();

        if ((nombre == null || nombre.isBlank()) && nombreCompleto != null && !nombreCompleto.isBlank()) {
            String[] partes = nombreCompleto.trim().split("\\s+", 2);
            nombre = partes[0];

            if (partes.length > 1) {
                apellidos = partes[1];
            }
        }

        if (nombre == null || nombre.isBlank()) {
            nombre = "Usuario";
        }

        if (apellidos == null) {
            apellidos = "";
        }

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) {
            RolUsuario rolUser = rolUsuarioRepository.findByNombreIgnoreCase("USER")
                    .orElse(null);

            if (rolUser == null) {
                response.sendRedirect(request.getContextPath() + "/login?error=rol-user");
                return;
            }

            usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setApellidos(apellidos);
            usuario.setEmail(email);
            usuario.setContrasena(passwordEncoder.encode(UUID.randomUUID().toString()));
            usuario.setRol(rolUser);

            usuarioRepository.save(usuario);
        }

        response.sendRedirect(request.getContextPath() + "/inicio");
    }

    private String obtenerTexto(Map<String, Object> atributos, String clave) {
        Object valor = atributos.get(clave);

        if (valor == null) {
            return null;
        }

        String texto = valor.toString().trim();

        return texto.isBlank() ? null : texto;
    }
}