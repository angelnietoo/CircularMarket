package com.circularmarket.demo.controller;

import com.circularmarket.demo.model.Categoria;
import com.circularmarket.demo.model.Producto;
import com.circularmarket.demo.repository.CategoriaRepository;
import com.circularmarket.demo.repository.ProductoRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class InicioController {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public InicioController(CategoriaRepository categoriaRepository,
                            ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        List<Categoria> categorias = categoriaRepository.findByActivaTrueOrderByNombreAsc();

        Map<Categoria, List<Producto>> productosPorCategoria = new LinkedHashMap<>();

        for (Categoria categoria : categorias) {
            List<Producto> productos = productoRepository
                    .findTop4ByCategoria_IdAndActivoTrueOrderByCreadoEnDesc(categoria.getId());

            if (!productos.isEmpty()) {
                productosPorCategoria.put(categoria, productos);
            }
        }

        model.addAttribute("categoriasHeader", categorias);
        model.addAttribute("productosPorCategoria", productosPorCategoria);
        model.addAttribute("busqueda", "");

        return "inicio";
    }

    @GetMapping("/carrito")
    public String carrito(Model model) {
        model.addAttribute("categoriasHeader", categoriaRepository.findByActivaTrueOrderByNombreAsc());
        model.addAttribute("busqueda", "");

        return "carrito";
    }

    @GetMapping("/productos")
    public String productos(@RequestParam(value = "q", required = false) String q,
                            Model model) {
        List<Categoria> categorias = categoriaRepository.findByActivaTrueOrderByNombreAsc();

        model.addAttribute("categoriasHeader", categorias);

        if (q != null && !q.trim().isBlank()) {
            String busqueda = q.trim();

            model.addAttribute("productos", productoRepository.buscarProductosActivos(busqueda));
            model.addAttribute("busqueda", busqueda);
        } else {
            model.addAttribute("productos", productoRepository.findByActivoTrueOrderByCreadoEnDesc());
            model.addAttribute("busqueda", "");
        }

        return "productos";
    }

    @GetMapping("/productos/imagen/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerImagenProducto(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id).orElse(null);

        if (producto == null || producto.getImagen() == null || producto.getImagen().length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(producto.getImagen());
    }
}