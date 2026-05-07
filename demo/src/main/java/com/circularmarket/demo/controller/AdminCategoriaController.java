package com.circularmarket.demo.controller;

import com.circularmarket.demo.model.Categoria;
import com.circularmarket.demo.repository.CategoriaRepository;
import com.circularmarket.demo.repository.ProductoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categorias")
public class AdminCategoriaController {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public AdminCategoriaController(CategoriaRepository categoriaRepository,
                                    ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "admin/categorias-lista";
    }

    @GetMapping("/nueva")
    public String nuevaCategoria(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "admin/categorias-formulario";
    }

    @GetMapping("/{id}/editar")
    public String editarCategoria(@PathVariable Long id, Model model) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        model.addAttribute("categoria", categoria);
        return "admin/categorias-formulario";
    }

    @PostMapping("/guardar")
    public String guardarNuevaCategoria(@ModelAttribute("categoria") Categoria categoria,
                                        Model model) {

        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            model.addAttribute("categoria", categoria);
            model.addAttribute("error", "El nombre de la categoría es obligatorio.");
            return "admin/categorias-formulario";
        }

        try {
            categoriaRepository.save(categoria);
            return "redirect:/admin/categorias";
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("categoria", categoria);
            model.addAttribute("error", "Ya existe una categoría con ese nombre.");
            return "admin/categorias-formulario";
        }
    }

    @PostMapping("/guardar/{id}")
    public String actualizarCategoria(@PathVariable Long id,
                                      @ModelAttribute("categoria") Categoria categoriaForm,
                                      Model model) {

        if (categoriaForm.getNombre() == null || categoriaForm.getNombre().trim().isEmpty()) {
            model.addAttribute("categoria", categoriaForm);
            model.addAttribute("error", "El nombre de la categoría es obligatorio.");
            return "admin/categorias-formulario";
        }

        try {
            Categoria categoria = categoriaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

            categoria.setNombre(categoriaForm.getNombre());
            categoria.setDescripcion(categoriaForm.getDescripcion());
            categoria.setActiva(categoriaForm.isActiva());

            categoriaRepository.save(categoria);
            return "redirect:/admin/categorias";
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("categoria", categoriaForm);
            model.addAttribute("error", "Ya existe una categoría con ese nombre.");
            return "admin/categorias-formulario";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarCategoria(@PathVariable Long id, Model model) {
        try {
            if (productoRepository.existsByCategoria_Id(id)) {
                model.addAttribute("categorias", categoriaRepository.findAll());
                model.addAttribute("error", "No se puede eliminar la categoría porque tiene productos asociados.");
                return "admin/categorias-lista";
            }

            categoriaRepository.deleteById(id);
            return "redirect:/admin/categorias";
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("error", "No se puede eliminar la categoría porque tiene productos asociados.");
            return "admin/categorias-lista";
        }
    }
}