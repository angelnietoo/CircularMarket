package com.circularmarket.demo.controller;

import com.circularmarket.demo.model.Categoria;
import com.circularmarket.demo.model.Producto;
import com.circularmarket.demo.repository.CategoriaRepository;
import com.circularmarket.demo.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public AdminProductoController(ProductoRepository productoRepository,
                                   CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public String listarProductos(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        Page<Producto> paginaProductos = productoRepository.findAll(pageable);

        model.addAttribute("productos", paginaProductos.getContent());
        model.addAttribute("paginaProductos", paginaProductos);

        return "admin/productos-lista";
    }

    @GetMapping("/nuevo")
    public String nuevoProducto(Model model) {
        Producto producto = new Producto();
        producto.setActivo(true);

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaRepository.findByActivaTrueOrderByNombreAsc());

        return "admin/productos-formulario";
    }

    @GetMapping("/{id}/editar")
    public String editarProducto(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaRepository.findByActivaTrueOrderByNombreAsc());

        return "admin/productos-formulario";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute("producto") Producto formProducto,
                                  @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                                  @RequestParam(value = "imagenArchivo", required = false) MultipartFile imagenArchivo,
                                  Model model) throws IOException {

        if (categoriaId == null) {
            model.addAttribute("error", "Debes seleccionar una categoría.");
            model.addAttribute("categorias", categoriaRepository.findByActivaTrueOrderByNombreAsc());
            model.addAttribute("producto", formProducto);
            return "admin/productos-formulario";
        }

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        Producto producto;

        if (formProducto.getId() != null) {
            producto = productoRepository.findById(formProducto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        } else {
            producto = new Producto();
        }

        producto.setTitulo(formProducto.getTitulo());
        producto.setDescripcion(formProducto.getDescripcion());
        producto.setPrecio(formProducto.getPrecio());
        producto.setStock(formProducto.getStock());
        producto.setActivo(formProducto.getActivo() != null ? formProducto.getActivo() : false);
        producto.setCategoria(categoria);

        if (imagenArchivo != null && !imagenArchivo.isEmpty()) {
            producto.setImagen(imagenArchivo.getBytes());
            producto.setImagenTipo(imagenArchivo.getContentType());
        }

        productoRepository.save(producto);

        return "redirect:/admin/productos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarProducto(@PathVariable Long id,
                                   @RequestParam(defaultValue = "0") int page) {
        productoRepository.deleteById(id);
        return "redirect:/admin/productos?page=" + page;
    }
}