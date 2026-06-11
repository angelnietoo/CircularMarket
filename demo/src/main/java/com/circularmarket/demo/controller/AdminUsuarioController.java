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

    // Muestra el listado de usuarios con búsqueda y paginación.
    @GetMapping
    public String listarUsuarios(
            @RequestParam(name = "q", required = false) String q,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        String busqueda = q != null ? q.trim() : "";

        Page<Usuario> paginaUsuarios;

        // Si no hay búsqueda, muestra todos los usuarios paginados.
        if (busqueda.isBlank()) {
            paginaUsuarios = usuarioRepository.findAll(pageable);
        } else {
            // Si hay búsqueda, filtra los usuarios por el texto introducido.
            paginaUsuarios = usuarioRepository.buscarUsuarios(busqueda, pageable);
        }

        model.addAttribute("usuarios", paginaUsuarios.getContent());
        model.addAttribute("paginaUsuarios", paginaUsuarios);
        model.addAttribute("q", busqueda);

        return "admin/usuarios-lista";
    }

    // Muestra el detalle de un usuario concreto.
    @GetMapping("/{id}/ver")
    public String verUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);

        // Si el usuario no existe, vuelve al listado.
        if (usuario == null) {
            return "redirect:/admin/usuarios";
        }

        model.addAttribute("usuario", usuario);

        return "admin/usuarios-detalle";
    }

    // Abre el formulario para crear un nuevo usuario.
    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());

        // Carga los roles disponibles para mostrarlos en el formulario.
        model.addAttribute("roles", rolUsuarioRepository.findAll());

        return "admin/usuarios-formulario";
    }

    // Abre el formulario para editar un usuario existente.
    @GetMapping("/{id}/editar")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);

        // Si el usuario no existe, vuelve al listado.
        if (usuario == null) {
            return "redirect:/admin/usuarios";
        }

        model.addAttribute("usuario", usuario);

        // Carga los roles para poder cambiar el rol del usuario.
        model.addAttribute("roles", rolUsuarioRepository.findAll());

        return "admin/usuarios-formulario";
    }

    // Guarda un usuario nuevo o actualiza uno existente desde el panel admin.
    @PostMapping
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario,
                                 Model model) {
        try {
            usuarioService.guardarUsuarioAdmin(usuario);
            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException e) {
            // Si hay algún error de validación, vuelve al formulario con el mensaje.
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", rolUsuarioRepository.findAll());
            return "admin/usuarios-formulario";
        }
    }

    // Elimina un usuario desde el panel de administración.
    @PostMapping("/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(name = "q", required = false) String q) {

        usuarioService.eliminarPorId(id);

        String busqueda = q != null ? q.trim() : "";

        // Mantiene la página actual al volver al listado.
        if (busqueda.isBlank()) {
            return "redirect:/admin/usuarios?page=" + page;
        }

        // Mantiene también el filtro de búsqueda si estaba activo.
        String qCodificada = URLEncoder.encode(busqueda, StandardCharsets.UTF_8);

        return "redirect:/admin/usuarios?page=" + page + "&q=" + qCodificada;
    }
}