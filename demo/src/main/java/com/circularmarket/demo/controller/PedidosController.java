package com.circularmarket.demo.controller;

import com.circularmarket.demo.model.Categoria;
import com.circularmarket.demo.model.Pedido;
import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.CategoriaRepository;
import com.circularmarket.demo.repository.PedidoRepository;
import com.circularmarket.demo.repository.UsuarioRepository;
import com.circularmarket.demo.service.UsuarioAutenticado;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PedidosController {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public PedidosController(PedidoRepository pedidoRepository,
                             UsuarioRepository usuarioRepository,
                             CategoriaRepository categoriaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // Muestra los pedidos del usuario autenticado.
    @GetMapping("/pedidos")
    public String verPedidos(Model model, Authentication authentication) {
        Long usuarioId = obtenerUsuarioIdAutenticado(authentication);

        // Si no hay usuario autenticado, se redirige al login.
        if (usuarioId == null) {
            return "redirect:/login";
        }

        // Busca los pedidos del usuario ordenados desde el más reciente.
        List<Pedido> pedidos = pedidoRepository.findByCompradorIdOrderByFechaPedidoDesc(usuarioId.intValue());

        model.addAttribute("pedidos", pedidos);

        cargarDatosHeader(model);

        return "pedidos";
    }

    // Carga los datos necesarios para mostrar el header.
    private void cargarDatosHeader(Model model) {
        List<Categoria> categorias = categoriaRepository.findByActivaTrueOrderByNombreAsc();

        model.addAttribute("categoriasHeader", categorias);
        model.addAttribute("categoriaSeleccionadaId", null);
        model.addAttribute("busqueda", "");
    }

    // Obtiene el ID del usuario autenticado según el tipo de login usado.
    private Long obtenerUsuarioIdAutenticado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // Caso de login normal con usuario propio de la aplicación.
        if (principal instanceof UsuarioAutenticado usuarioAutenticado) {
            return usuarioAutenticado.getId();
        }

        // Caso de login con Google OAuth2.
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

        // Caso alternativo usando el email guardado en la autenticación.
        String email = authentication.getName();

        if (email == null || email.isBlank()) {
            return null;
        }

        return usuarioRepository.findByEmail(email.trim().toLowerCase())
                .map(Usuario::getId)
                .orElse(null);
    }
}