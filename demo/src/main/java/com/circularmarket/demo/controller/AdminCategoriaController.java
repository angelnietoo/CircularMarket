package com.circularmarket.demo.controller;

import com.circularmarket.demo.model.Categoria;
import com.circularmarket.demo.repository.CategoriaRepository;
import com.circularmarket.demo.repository.ProductoRepository;
import org.springframework.dao.DataIntegrityViolationException;
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
@RequestMapping("/admin/categorias")
public class AdminCategoriaController {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public AdminCategoriaController(CategoriaRepository categoriaRepository,
                                    ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    // Muestra el listado de categorías con búsqueda y paginación.
    @GetMapping
    public String listarCategorias(
            @RequestParam(name = "q", required = false) String q,
            @PageableDefault(size = 20, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable,
            Model model) {

        cargarCategoriasPaginadas(model, pageable, q);

        return "admin/categorias-lista";
    }

    // Muestra el detalle de una categoría concreta.
    @GetMapping("/{id}/ver")
    public String verCategoria(@PathVariable Long id, Model model) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        // Cuenta los productos activos asociados a esta categoría.
        long totalProductosCategoria = productoRepository.findByCategoria_IdAndActivoTrueOrderByCreadoEnDesc(id).size();

        model.addAttribute("categoria", categoria);
        model.addAttribute("totalProductosCategoria", totalProductosCategoria);

        return "admin/categorias-detalle";
    }

    // Abre el formulario para crear una nueva categoría.
    @GetMapping("/nueva")
    public String nuevaCategoria(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "admin/categorias-formulario";
    }

    // Abre el formulario para editar una categoría existente.
    @GetMapping("/{id}/editar")
    public String editarCategoria(@PathVariable Long id, Model model) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        model.addAttribute("categoria", categoria);

        return "admin/categorias-formulario";
    }

    // Guarda una nueva categoría en la base de datos.
    @PostMapping("/guardar")
    public String guardarNuevaCategoria(@ModelAttribute("categoria") Categoria categoria,
                                        Model model) {

        // Valida que el nombre sea obligatorio.
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            model.addAttribute("categoria", categoria);
            model.addAttribute("error", "El nombre de la categoría es obligatorio.");
            return "admin/categorias-formulario";
        }

        try {
            categoriaRepository.save(categoria);
            return "redirect:/admin/categorias";
        } catch (DataIntegrityViolationException ex) {
            // Evita guardar una categoría con un nombre ya existente.
            model.addAttribute("categoria", categoria);
            model.addAttribute("error", "Ya existe una categoría con ese nombre.");
            return "admin/categorias-formulario";
        }
    }

    // Actualiza los datos de una categoría existente.
    @PostMapping("/guardar/{id}")
    public String actualizarCategoria(@PathVariable Long id,
                                      @ModelAttribute("categoria") Categoria categoriaForm,
                                      Model model) {

        // Comprueba que el nombre no esté vacío.
        if (categoriaForm.getNombre() == null || categoriaForm.getNombre().trim().isEmpty()) {
            model.addAttribute("categoria", categoriaForm);
            model.addAttribute("error", "El nombre de la categoría es obligatorio.");
            return "admin/categorias-formulario";
        }

        try {
            Categoria categoria = categoriaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

            // Actualiza solo los campos editables del formulario.
            categoria.setNombre(categoriaForm.getNombre());
            categoria.setDescripcion(categoriaForm.getDescripcion());
            categoria.setActiva(categoriaForm.isActiva());

            categoriaRepository.save(categoria);

            return "redirect:/admin/categorias";
        } catch (DataIntegrityViolationException ex) {
            // Controla el caso de nombre duplicado.
            model.addAttribute("categoria", categoriaForm);
            model.addAttribute("error", "Ya existe una categoría con ese nombre.");
            return "admin/categorias-formulario";
        }
    }

    // Elimina una categoría si no tiene productos asociados.
    @PostMapping("/{id}/eliminar")
    public String eliminarCategoria(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "q", required = false) String q,
            @PageableDefault(size = 20, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable,
            Model model) {

        String busqueda = q != null ? q.trim() : "";

        try {
            // No permite borrar categorías que todavía tienen productos.
            if (productoRepository.existsByCategoria_Id(id)) {
                cargarCategoriasPaginadas(model, pageable, busqueda);
                model.addAttribute("error", "No se puede eliminar la categoría porque tiene productos asociados.");
                return "admin/categorias-lista";
            }

            categoriaRepository.deleteById(id);

            // Mantiene la página actual al volver al listado.
            if (busqueda.isBlank()) {
                return "redirect:/admin/categorias?page=" + page;
            }

            // Mantiene también el texto de búsqueda si estaba filtrando.
            String qCodificada = URLEncoder.encode(busqueda, StandardCharsets.UTF_8);

            return "redirect:/admin/categorias?page=" + page + "&q=" + qCodificada;

        } catch (DataIntegrityViolationException ex) {
            // Control extra por si la base de datos bloquea el borrado.
            cargarCategoriasPaginadas(model, pageable, busqueda);
            model.addAttribute("error", "No se puede eliminar la categoría porque tiene productos asociados.");
            return "admin/categorias-lista";
        }
    }

    // Carga las categorías según la búsqueda y prepara los datos para la vista.
    private void cargarCategoriasPaginadas(Model model, Pageable pageable, String q) {
        String busqueda = q != null ? q.trim() : "";

        Page<Categoria> paginaCategorias;

        if (busqueda.isBlank()) {
            paginaCategorias = categoriaRepository.findAll(pageable);
        } else {
            paginaCategorias = categoriaRepository.buscarCategoriasAdmin(busqueda, pageable);
        }

        model.addAttribute("categorias", paginaCategorias.getContent());
        model.addAttribute("paginaCategorias", paginaCategorias);
        model.addAttribute("q", busqueda);
    }
}