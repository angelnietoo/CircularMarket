package com.circularmarket.demo.controller;

<<<<<<< HEAD
import com.circularmarket.demo.model.RolUsuario;
import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.RolUsuarioRepository;
import com.circularmarket.demo.repository.UsuarioRepository;
import com.circularmarket.demo.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
=======
import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.RolUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
>>>>>>> bbf15444231fb4b62d5f4a4c11e53092aa22254a

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

<<<<<<< HEAD
    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioRepository usuarioRepository,
                                  RolUsuarioRepository rolUsuarioRepository,
                                  UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin/usuarios-lista";
    }
=======
    @Autowired
    private RolUsuarioRepository rolUsuarioRepository;
>>>>>>> bbf15444231fb4b62d5f4a4c11e53092aa22254a

    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolUsuarioRepository.findAll());
        return "admin/usuarios-formulario";
    }
<<<<<<< HEAD

    @GetMapping("/{id}/editar")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario == null) {
            return "redirect:/admin/usuarios";
        }

        if (usuario.getRolUsuario() != null) {
            usuario.setRolId(usuario.getRolUsuario().getId());
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolUsuarioRepository.findAll());
        return "admin/usuarios-formulario";
    }

    @PostMapping
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario, Model model) {
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
=======
>>>>>>> bbf15444231fb4b62d5f4a4c11e53092aa22254a
}