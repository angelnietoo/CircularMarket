package com.circularmarket.demo.service;

import com.circularmarket.demo.dto.RegistroRequest;
import com.circularmarket.demo.model.RolUsuario;
import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.RolUsuarioRepository;
import com.circularmarket.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolUsuarioRepository rolUsuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrar(RegistroRequest request) {
        String email = normalizarEmail(request.getEmail());

        if (email == null) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Este email ya está registrado.");
        }

        RolUsuario rol = rolUsuarioRepository.findByNombre("user")
                .orElseGet(() -> rolUsuarioRepository.save(new RolUsuario("user")));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(email);
        usuario.setContrasena(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        String emailNormalizado = normalizarEmail(email);

        if (emailNormalizado == null) {
            return null;
        }

        return usuarioRepository.findByEmail(emailNormalizado).orElse(null);
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

        String emailNuevo = normalizarEmail(datosFormulario.getEmail());

        if (emailNuevo == null) {
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

    @Transactional
    public Usuario guardarUsuarioAdmin(Usuario datosFormulario) {
        Usuario usuario;

        if (datosFormulario.getId() == null) {
            usuario = new Usuario();

            if (datosFormulario.getContrasena() == null || datosFormulario.getContrasena().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria.");
            }
        } else {
            usuario = usuarioRepository.findById(datosFormulario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        }

        String email = normalizarEmail(datosFormulario.getEmail());
        if (email == null) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }

        if (!email.equalsIgnoreCase(usuario.getEmail()) && usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Este email ya está registrado.");
        }

        RolUsuario rolSeleccionado = resolverRol(datosFormulario, usuario);

        usuario.setNombre(datosFormulario.getNombre());
        usuario.setApellidos(datosFormulario.getApellidos());
        usuario.setEmail(email);
        usuario.setTelefono(datosFormulario.getTelefono());
        usuario.setDireccion(datosFormulario.getDireccion());
        usuario.setRolUsuario(rolSeleccionado);
        usuario.setRol(rolSeleccionado.getNombre());

        if (datosFormulario.getContrasena() != null && !datosFormulario.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(datosFormulario.getContrasena()));
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo.");
        }

        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        usuarioRepository.deleteById(id);
    }

    private RolUsuario resolverRol(Usuario datosFormulario, Usuario usuario) {
        if (datosFormulario.getRolId() != null) {
            return rolUsuarioRepository.findById(datosFormulario.getRolId())
                    .orElseThrow(() -> new IllegalArgumentException("El rol seleccionado no existe."));
        }

        if (usuario.getRolUsuario() != null) {
            return usuario.getRolUsuario();
        }

        return rolUsuarioRepository.findAll().stream()
                .filter(r -> "USER".equalsIgnoreCase(r.getNombre()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Debes seleccionar un rol."));
    }

    private String normalizarEmail(String email) {
        if (email == null) {
            return null;
        }
        String normalizado = email.trim().toLowerCase();
        return normalizado.isBlank() ? null : normalizado;
    }
}