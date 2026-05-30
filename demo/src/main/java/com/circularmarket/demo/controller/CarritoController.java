package com.circularmarket.demo.controller;

import com.circularmarket.demo.dto.CarritoItem;
import com.circularmarket.demo.model.Categoria;
import com.circularmarket.demo.repository.CategoriaRepository;
import com.circularmarket.demo.repository.UsuarioRepository;
import com.circularmarket.demo.service.CarritoService;
import com.circularmarket.demo.service.UsuarioAutenticado;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CarritoController {

    private static final BigDecimal GASTOS_ENVIO = BigDecimal.valueOf(4.99);

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoService carritoService;

    public CarritoController(CategoriaRepository categoriaRepository,
                             UsuarioRepository usuarioRepository,
                             CarritoService carritoService) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.carritoService = carritoService;
    }

    @GetMapping("/carrito")
    public String verCarrito(Model model, Authentication authentication) {
        Long usuarioId = obtenerUsuarioIdAutenticado(authentication);

        if (usuarioId == null) {
            return "redirect:/login";
        }

        cargarDatosCarrito(model, usuarioId);
        cargarDatosHeader(model);

        return "carrito";
    }

    @PostMapping("/carrito/anadir/{id}")
    public String anadirAlCarrito(@PathVariable Long id,
                                  Authentication authentication,
                                  @RequestHeader(value = "Referer", required = false) String referer) {
        Long usuarioId = obtenerUsuarioIdAutenticado(authentication);

        if (usuarioId == null) {
            return "redirect:/login";
        }

        carritoService.anadirProducto(usuarioId, id);

        return redireccionSegura(referer);
    }

    @PostMapping("/comprar-ya/{id}")
    public String comprarYa(@PathVariable Long id, Authentication authentication) {
        Long usuarioId = obtenerUsuarioIdAutenticado(authentication);

        if (usuarioId == null) {
            return "redirect:/login";
        }

        carritoService.anadirProducto(usuarioId, id);

        return "redirect:/carrito";
    }

    @PostMapping("/carrito/aumentar/{id}")
    public String aumentarCantidad(@PathVariable Long id, Authentication authentication) {
        Long usuarioId = obtenerUsuarioIdAutenticado(authentication);

        if (usuarioId == null) {
            return "redirect:/login";
        }

        carritoService.aumentarCantidad(usuarioId, id);

        return "redirect:/carrito";
    }

    @PostMapping("/carrito/disminuir/{id}")
    public String disminuirCantidad(@PathVariable Long id, Authentication authentication) {
        Long usuarioId = obtenerUsuarioIdAutenticado(authentication);

        if (usuarioId == null) {
            return "redirect:/login";
        }

        carritoService.disminuirCantidad(usuarioId, id);

        return "redirect:/carrito";
    }

    @PostMapping("/carrito/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, Authentication authentication) {
        Long usuarioId = obtenerUsuarioIdAutenticado(authentication);

        if (usuarioId == null) {
            return "redirect:/login";
        }

        carritoService.eliminarProducto(usuarioId, id);

        return "redirect:/carrito";
    }

    private void cargarDatosCarrito(Model model, Long usuarioId) {
        List<CarritoItem> carritoItems = carritoService.obtenerItems(usuarioId);

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CarritoItem item : carritoItems) {
            subtotal = subtotal.add(item.getTotal());
        }

        BigDecimal envio = carritoItems.isEmpty() ? BigDecimal.ZERO : GASTOS_ENVIO;
        BigDecimal total = subtotal.add(envio);

        model.addAttribute("carritoItems", carritoItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("envio", envio);
        model.addAttribute("total", total);
    }

    private void cargarDatosHeader(Model model) {
        List<Categoria> categorias = categoriaRepository.findByActivaTrueOrderByNombreAsc();

        model.addAttribute("categoriasHeader", categorias);
        model.addAttribute("categoriaSeleccionadaId", null);
        model.addAttribute("busqueda", "");
    }

    private Long obtenerUsuarioIdAutenticado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
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
                    .map(usuario -> usuario.getId())
                    .orElse(null);
        }

        String email = authentication.getName();

        if (email == null || email.isBlank()) {
            return null;
        }

        return usuarioRepository.findByEmail(email.trim().toLowerCase())
                .map(usuario -> usuario.getId())
                .orElse(null);
    }

    private String redireccionSegura(String referer) {
        if (referer == null || referer.isBlank()) {
            return "redirect:/productos";
        }

        return "redirect:" + referer;
    }
}