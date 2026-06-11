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

    // Registra un usuario nuevo desde el formulario de registro.
    @Transactional
    public Usuario registrar(RegistroRequest request) {

        String email = normalizarEmail(request.getEmail());

        if (email == null) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Este email ya está registrado.");
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(email);

        // La contraseña se guarda cifrada, nunca en texto plano.
        usuario.setContrasena(passwordEncoder.encode(request.getPassword()));
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());

        // Todo usuario registrado desde la web empieza con rol USER.
        RolUsuario rolUser = rolUsuarioRepository.findByNombre("USER")
                .orElseThrow(() -> new IllegalArgumentException("Rol USER no encontrado"));

        usuario.setRol(rolUser);

        return usuarioRepository.save(usuario);
    }

    // Busca un usuario por email.
    public Usuario buscarPorEmail(String email) {

        String emailNormalizado = normalizarEmail(email);

        if (emailNormalizado == null) {
            return null;
        }

        return usuarioRepository.findByEmail(emailNormalizado).orElse(null);
    }

    // Busca un usuario por su ID.
    public Usuario buscarPorId(Long id) {

        if (id == null) {
            return null;
        }

        return usuarioRepository.findById(id).orElse(null);
    }

    // Actualiza los datos de un usuario existente.
    @Transactional
    public Usuario actualizarUsuario(Long id,
                                     Usuario datosFormulario,
                                     String passwordNueva,
                                     String repetirPassword) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se ha encontrado el usuario."));

        String emailNuevo = normalizarEmail(datosFormulario.getEmail());

        if (emailNuevo == null) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }

        // Si cambia el email, se comprueba que no lo use otro usuario.
        if (!emailNuevo.equalsIgnoreCase(usuario.getEmail())
                && usuarioRepository.existsByEmail(emailNuevo)) {

            throw new IllegalArgumentException("Este email ya está registrado.");
        }

        usuario.setNombre(datosFormulario.getNombre());
        usuario.setApellidos(datosFormulario.getApellidos());
        usuario.setEmail(emailNuevo);
        usuario.setTelefono(datosFormulario.getTelefono());
        usuario.setDireccion(datosFormulario.getDireccion());

        // Solo cambia la contraseña si se escribe una nueva.
        if (passwordNueva != null && !passwordNueva.isBlank()) {

            if (!passwordNueva.equals(repetirPassword)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }

            usuario.setContrasena(passwordEncoder.encode(passwordNueva));
        }

        return usuarioRepository.save(usuario);
    }

    // Crea o edita usuarios desde el panel de administración.
    @Transactional
    public Usuario guardarUsuarioAdmin(Usuario datosFormulario) {

        Usuario usuario;

        if (datosFormulario.getId() == null) {

            usuario = new Usuario();

            // Al crear un usuario nuevo, la contraseña es obligatoria.
            if (datosFormulario.getContrasena() == null
                    || datosFormulario.getContrasena().isBlank()) {

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

        // Evita emails duplicados entre usuarios.
        if (!email.equalsIgnoreCase(usuario.getEmail())
                && usuarioRepository.existsByEmail(email)) {

            throw new IllegalArgumentException("Este email ya está registrado.");
        }

        usuario.setNombre(datosFormulario.getNombre());
        usuario.setApellidos(datosFormulario.getApellidos());
        usuario.setEmail(email);
        usuario.setTelefono(datosFormulario.getTelefono());
        usuario.setDireccion(datosFormulario.getDireccion());

        if (datosFormulario.getRol() != null) {

            // Se carga el rol real desde la base de datos.
            RolUsuario rol = rolUsuarioRepository.findById(
                    datosFormulario.getRol().getId()
            ).orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

            usuario.setRol(rol);

        } else {

            // Si no se indica rol, se asigna USER por defecto.
            RolUsuario rolUser = rolUsuarioRepository.findByNombre("USER")
                    .orElseThrow(() -> new IllegalArgumentException("Rol USER no encontrado"));

            usuario.setRol(rolUser);
        }

        // Si se escribe contraseña, se actualiza cifrada.
        if (datosFormulario.getContrasena() != null
                && !datosFormulario.getContrasena().isBlank()) {

            usuario.setContrasena(
                    passwordEncoder.encode(datosFormulario.getContrasena())
            );
        }

        return usuarioRepository.save(usuario);
    }

    // Elimina un usuario por ID.
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

    // Limpia el email para guardarlo siempre igual.
    private String normalizarEmail(String email) {

        if (email == null) {
            return null;
        }

        String normalizado = email.trim().toLowerCase();

        return normalizado.isBlank() ? null : normalizado;
    }
}