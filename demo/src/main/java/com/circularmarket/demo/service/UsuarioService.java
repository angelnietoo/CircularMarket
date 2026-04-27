package com.circularmarket.demo.service;

import com.circularmarket.demo.dto.RegistroRequest;
import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(RegistroRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El usuario ya existe.");
        }

        Usuario usuario = new Usuario();

        // ===== IDENTIDAD =====
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());

        // ===== CREDENCIALES =====
        usuario.setEmail(email);
        usuario.setContrasena(passwordEncoder.encode(request.getPassword()));

        // ===== ROL POR DEFECTO =====
        usuario.setRol("user");

        // ===== CONTACTO =====
        usuario.setTelefono(request.getTelefono());

        return usuarioRepository.save(usuario);
    }
}
