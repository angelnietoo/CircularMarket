package com.circularmarket.demo.service;

import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificacionCorreoService {

    private final UsuarioRepository usuarioRepository;
    private final JavaMailSender javaMailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String correoRemitente;

    public VerificacionCorreoService(UsuarioRepository usuarioRepository,
                                      JavaMailSender javaMailSender) {
        this.usuarioRepository = usuarioRepository;
        this.javaMailSender = javaMailSender;
    }

    // Genera el token de verificación y envía el correo al usuario.
    @Transactional
    public void enviarCorreoVerificacion(Usuario usuario) {
        String token = UUID.randomUUID().toString();

        usuario.setEmailVerificado(false);
        usuario.setEmailVerificacionToken(token);
        usuario.setEmailVerificacionExpira(LocalDateTime.now().plusHours(24));

        usuarioRepository.save(usuario);

        enviarCorreo(usuario, token);
    }

    // Comprueba el token recibido y marca el correo como verificado.
    @Transactional
    public void verificarCorreo(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El enlace de verificación no es válido.");
        }

        Usuario usuario = usuarioRepository.findByEmailVerificacionToken(token)
                .orElseThrow(() -> new IllegalArgumentException("El enlace de verificación no es válido."));

        if (usuario.getEmailVerificacionExpira() == null) {
            throw new IllegalArgumentException("El enlace de verificación no es válido.");
        }

        if (usuario.getEmailVerificacionExpira().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El enlace de verificación ha caducado.");
        }

        usuario.setEmailVerificado(true);
        usuario.setEmailVerificacionToken(null);
        usuario.setEmailVerificacionExpira(null);

        usuarioRepository.save(usuario);
    }

    // Envía el correo con el enlace de verificación.
    private void enviarCorreo(Usuario usuario, String token) {
        String enlace = baseUrl + "/verificar-correo?token=" + token;

        String cuerpo = """
                Hola %s,

                Gracias por registrarte en CircularMarket.

                Para verificar que este correo es tuyo, pulsa en el siguiente enlace:

                %s

                Este enlace caduca en 24 horas.

                Si no has creado una cuenta en CircularMarket, puedes ignorar este correo.
                """.formatted(usuario.getNombre(), enlace);

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(correoRemitente);
        mensaje.setTo(usuario.getEmail());
        mensaje.setSubject("Verifica tu correo - CircularMarket");
        mensaje.setText(cuerpo);

        javaMailSender.send(mensaje);
    }
}