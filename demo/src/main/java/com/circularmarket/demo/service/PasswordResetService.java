package com.circularmarket.demo.service;

import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String correoRemitente;

    public PasswordResetService(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder,
                                JavaMailSender javaMailSender) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
    }

    // Solicita el restablecimiento de contraseña para un email.
    @Transactional
    public void solicitarRestablecimiento(String email) {

        // Limpia el email antes de buscarlo en la base de datos.
        String emailNormalizado = normalizarEmail(email);

        if (emailNormalizado == null) {
            return;
        }

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado).orElse(null);

        /*
         * No mostramos error aunque el correo no exista.
         * Así evitamos que alguien pueda comprobar qué emails están registrados.
         */
        if (usuario == null) {
            return;
        }

        // Genera un token único para el enlace de recuperación.
        String token = UUID.randomUUID().toString();

        // Guarda el token y su fecha de caducidad.
        usuario.setResetToken(token);
        usuario.setResetTokenExpira(LocalDateTime.now().plusMinutes(30));

        usuarioRepository.save(usuario);

        // Envía el correo con el enlace para cambiar la contraseña.
        enviarCorreoRestablecimiento(usuario, token);
    }

    // Busca un usuario usando un token válido de recuperación.
    public Usuario obtenerUsuarioPorTokenValido(String token) {

        // Comprueba que el token no esté vacío.
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El enlace no es válido.");
        }

        // Busca el usuario asociado al token.
        Usuario usuario = usuarioRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("El enlace no es válido."));

        // Si no tiene fecha de caducidad, el enlace no es válido.
        if (usuario.getResetTokenExpira() == null) {
            throw new IllegalArgumentException("El enlace no es válido.");
        }

        // Comprueba si el enlace ya ha caducado.
        if (usuario.getResetTokenExpira().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El enlace ha caducado. Solicita uno nuevo.");
        }

        return usuario;
    }

    // Cambia la contraseña del usuario usando el token de recuperación.
    @Transactional
    public void restablecerContrasena(String token, String nuevaPassword, String repetirPassword) {

        // Comprueba que la nueva contraseña no esté vacía.
        if (nuevaPassword == null || nuevaPassword.isBlank()) {
            throw new IllegalArgumentException("Debes introducir una nueva contraseña.");
        }

        // Comprueba que la contraseña tenga una longitud mínima.
        if (nuevaPassword.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }

        // Comprueba que las dos contraseñas coincidan.
        if (!nuevaPassword.equals(repetirPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        // Busca el usuario asociado al token si el enlace sigue siendo válido.
        Usuario usuario = obtenerUsuarioPorTokenValido(token);

        // Guarda la nueva contraseña cifrada.
        usuario.setContrasena(passwordEncoder.encode(nuevaPassword));

        // Limpia el token para que no se pueda reutilizar.
        usuario.setResetToken(null);
        usuario.setResetTokenExpira(null);

        usuarioRepository.save(usuario);
    }

    // Envía el correo con el enlace para restablecer la contraseña.
    private void enviarCorreoRestablecimiento(Usuario usuario, String token) {

        // Construye el enlace que recibirá el usuario en el correo.
        String enlace = baseUrl + "/nueva-contrasena?token=" + token;

        String cuerpo = """
                Hola %s,

                Hemos recibido una solicitud para restablecer la contraseña de tu cuenta de CircularMarket.

                Para crear una nueva contraseña, entra en este enlace:

                %s

                Este enlace caduca en 30 minutos.

                Si no has solicitado este cambio, puedes ignorar este correo.
                """.formatted(usuario.getNombre(), enlace);

        // Prepara el mensaje de correo.
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(correoRemitente);
        mensaje.setTo(usuario.getEmail());
        mensaje.setSubject("Restablecer contraseña - CircularMarket");
        mensaje.setText(cuerpo);

        // Envía el correo al usuario.
        javaMailSender.send(mensaje);
    }

    // Limpia el email para guardarlo y buscarlo siempre con el mismo formato.
    private String normalizarEmail(String email) {

        if (email == null) {
            return null;
        }

        String normalizado = email.trim().toLowerCase();

        if (normalizado.isBlank()) {
            return null;
        }

        return normalizado;
    }
}