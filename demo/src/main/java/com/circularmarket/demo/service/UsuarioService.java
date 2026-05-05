package com.circularmarket.demo.service;

import com.circularmarket.demo.dto.RegistroRequest;
import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new IllegalArgumentException("Este email ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(email);
        usuario.setContrasena(passwordEncoder.encode(request.getPassword()));
        usuario.setRol("user");
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return usuarioRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
    }

    public Usuario buscarPorId(Long id) {
        if (id == null) {
            return null;
        }

        return usuarioRepository.findById(id).orElse(null);
    }

    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario datosFormulario, String passwordNueva, String repetirPassword) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se ha encontrado el usuario logueado."));

        String emailNuevo = datosFormulario.getEmail() != null
                ? datosFormulario.getEmail().trim().toLowerCase()
                : null;

        if (emailNuevo == null || emailNuevo.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }

        if (!emailNuevo.equalsIgnoreCase(usuario.getEmail()) && usuarioRepository.existsByEmail(emailNuevo)) {
            throw new IllegalArgumentException("Este email ya está registrado.");
        }

        usuario.setNombre(datosFormulario.getNombre());
        usuario.setApellidos(datosFormulario.getApellidos());
        usuario.setEmail(emailNuevo);
        usuario.setTelefono(datosFormulario.getTelefono());
        usuario.setDireccion(datosFormulario.getDireccion());

        if (passwordNueva != null && !passwordNueva.isBlank()) {
            if (!passwordNueva.equals(repetirPassword)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }
            usuario.setContrasena(passwordEncoder.encode(passwordNueva));
        }

        return usuarioRepository.save(usuario);
    }
}