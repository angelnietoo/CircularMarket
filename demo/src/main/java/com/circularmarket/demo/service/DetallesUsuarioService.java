package com.circularmarket.demo.service;

import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class DetallesUsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public DetallesUsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Carga el usuario por email cuando se intenta iniciar sesión.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException("Email vacío");
        }

        Usuario usuario = usuarioRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String rol = "USER";

        if (usuario.getRol() != null && usuario.getRol().getNombre() != null) {
            rol = usuario.getRol().getNombre().toUpperCase(Locale.ROOT);
        }

        // Convierte el rol del usuario en un permiso válido para Spring Security.
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rol);

        return new UsuarioAutenticado(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getContrasena(),
                List.of(authority),
                usuario.isEmailVerificado() // Bloquea el login si el correo no está verificado.
        );
    }
}