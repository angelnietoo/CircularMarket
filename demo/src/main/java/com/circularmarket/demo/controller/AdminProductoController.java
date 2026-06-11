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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    // Muestra el listado de productos con búsqueda y paginación.
    @GetMapping
    public String listarProductos(
            @RequestParam(name = "q", required = false) String q,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        String busqueda = q != null ? q.trim() : "";

        Page<Producto> paginaProductos;

        // Si no hay búsqueda, muestra todos los productos paginados.
        if (busqueda.isBlank()) {
            paginaProductos = productoRepository.findAll(pageable);
        } else {
            // Si hay búsqueda, filtra los productos por el texto introducido.
            paginaProductos = productoRepository.buscarProductosAdmin(busqueda, pageable);
        }

        model.addAttribute("productos", paginaProductos.getContent());
        model.addAttribute("paginaProductos", paginaProductos);
        model.addAttribute("q", busqueda);

        return "admin/productos-lista";
    }

    // Muestra el detalle de un producto concreto.
    @GetMapping("/{id}/ver")
    public String verProducto(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        model.addAttribute("producto", producto);

        return "admin/productos-detalle";
    }

    // Abre el formulario para crear un nuevo producto.
    @GetMapping("/nuevo")
    public String nuevoProducto(Model model) {
        Producto producto = new Producto();

        // Por defecto, el producto nuevo aparece como activo.
        producto.setActivo(true);

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaRepository.findByActivaTrueOrderByNombreAsc());

        return "admin/productos-formulario";
    }

    // Abre el formulario para editar un producto existente.
    @GetMapping("/{id}/editar")
    public String editarProducto(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaRepository.findByActivaTrueOrderByNombreAsc());

        return "admin/productos-formulario";
    }

    // Guarda un producto nuevo o actualiza uno existente.
    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute("producto") Producto formProducto,
                                  @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                                  @RequestParam(value = "imagenArchivo", required = false) MultipartFile imagenArchivo,
                                  Model model) throws IOException {

        // Comprueba que el producto tenga una categoría seleccionada.
        if (categoriaId == null) {
            model.addAttribute("error", "Debes seleccionar una categoría.");
            model.addAttribute("categorias", categoriaRepository.findByActivaTrueOrderByNombreAsc());
            model.addAttribute("producto", formProducto);
            return "admin/productos-formulario";
        }

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        Producto producto;

        // Si el formulario trae ID, se edita un producto existente.
        if (formProducto.getId() != null) {
            producto = productoRepository.findById(formProducto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        } else {
            // Si no trae ID, se crea un producto nuevo.
            producto = new Producto();
        }

        // Copia los datos del formulario al producto que se va a guardar.
        producto.setTitulo(formProducto.getTitulo());
        producto.setDescripcion(formProducto.getDescripcion());
        producto.setPrecio(formProducto.getPrecio());
        producto.setStock(formProducto.getStock());
        producto.setActivo(formProducto.getActivo() != null ? formProducto.getActivo() : false);
        producto.setCategoria(categoria);

        // Si se ha subido una imagen nueva, se guarda en el producto.
        if (imagenArchivo != null && !imagenArchivo.isEmpty()) {
            producto.setImagen(imagenArchivo.getBytes());
            producto.setImagenTipo(imagenArchivo.getContentType());
        }

        productoRepository.save(producto);

        return "redirect:/admin/productos";
    }

    // Elimina un producto desde el panel de administración.
    @PostMapping("/{id}/eliminar")
    public String eliminarProducto(@PathVariable Long id,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(name = "q", required = false) String q) {

        productoRepository.deleteById(id);

        String busqueda = q != null ? q.trim() : "";

        // Mantiene la página actual al volver al listado.
        if (busqueda.isBlank()) {
            return "redirect:/admin/productos?page=" + page;
        }

        // Mantiene también el filtro de búsqueda si estaba activo.
        String qCodificada = URLEncoder.encode(busqueda, StandardCharsets.UTF_8);

        return "redirect:/admin/productos?page=" + page + "&q=" + qCodificada;
    }
}