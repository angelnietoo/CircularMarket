package com.circularmarket.demo.controller;

import com.circularmarket.demo.model.RolUsuario;
import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.RolUsuarioRepository;
import com.circularmarket.demo.repository.UsuarioRepository;
import com.circularmarket.demo.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
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
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarPorId(id);
        return "redirect:/admin/usuarios";
    }
}