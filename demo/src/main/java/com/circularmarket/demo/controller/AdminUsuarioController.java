package com.circularmarket.demo.controller;

import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.RolUsuarioRepository;
import com.circularmarket.demo.repository.UsuarioRepository;
import com.circularmarket.demo.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final RolUsuarioRepository rolUsuarioRepository;

    public AdminUsuarioController(UsuarioRepository usuarioRepository,
                                  UsuarioService usuarioService,
                                  RolUsuarioRepository rolUsuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.rolUsuarioRepository = rolUsuarioRepository;
    }

    @GetMapping
    public String listarUsuarios(
            @RequestParam(name = "q", required = false) String q,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        String busqueda = q != null ? q.trim() : "";

        Page<Usuario> paginaUsuarios;

        if (busqueda.isBlank()) {
            paginaUsuarios = usuarioRepository.findAll(pageable);
        } else {
            paginaUsuarios = usuarioRepository.buscarUsuarios(busqueda, pageable);
        }

        model.addAttribute("usuarios", paginaUsuarios.getContent());
        model.addAttribute("paginaUsuarios", paginaUsuarios);
        model.addAttribute("q", busqueda);

        return "admin/usuarios-lista";
    }

    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolUsuarioRepository.findAll());

        return "admin/usuarios-formulario";
    }

    @GetMapping("/{id}/editar")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario == null) {
            return "redirect:/admin/usuarios";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolUsuarioRepository.findAll());

        return "admin/usuarios-formulario";
    }

    @PostMapping
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario,
                                 Model model) {
        try {
            usuarioService.guardarUsuarioAdmin(usuario);
            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", rolUsuarioRepository.findAll());
            return "admin/usuarios-formulario";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(name = "q", required = false) String q) {

        usuarioService.eliminarPorId(id);

        String busqueda = q != null ? q.trim() : "";

        if (busqueda.isBlank()) {
            return "redirect:/admin/usuarios?page=" + page;
        }

        String qCodificada = URLEncoder.encode(busqueda, StandardCharsets.UTF_8);

        return "redirect:/admin/usuarios?page=" + page + "&q=" + qCodificada;
    }
}