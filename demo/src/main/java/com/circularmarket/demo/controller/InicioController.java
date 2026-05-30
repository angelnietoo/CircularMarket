package com.circularmarket.demo.controller;

import com.circularmarket.demo.model.Categoria;
import com.circularmarket.demo.model.Producto;
import com.circularmarket.demo.repository.CategoriaRepository;
import com.circularmarket.demo.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

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
        model.addAttribute("categoriaSeleccionadaId", null);
        model.addAttribute("productosPorCategoria", productosPorCategoria);
        model.addAttribute("busqueda", "");

        return "inicio";
    }

    @GetMapping("/productos")
    public String productos(@RequestParam(value = "q", required = false) String q,
                            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                            Model model) {
        List<Categoria> categorias = categoriaRepository.findByActivaTrueOrderByNombreAsc();

        model.addAttribute("categoriasHeader", categorias);
        model.addAttribute("categoriaSeleccionadaId", categoriaId);

        if (categoriaId != null) {
            model.addAttribute("productos", productoRepository.findByCategoria_IdAndActivoTrueOrderByCreadoEnDesc(categoriaId));
            model.addAttribute("busqueda", "");
        } else if (q != null && !q.trim().isBlank()) {
            String busqueda = q.trim();

            model.addAttribute("productos", productoRepository.buscarProductosActivos(busqueda));
            model.addAttribute("busqueda", busqueda);
        } else {
            model.addAttribute("productos", productoRepository.findByActivoTrueOrderByCreadoEnDesc());
            model.addAttribute("busqueda", "");
        }

        return "productos";
    }

    @GetMapping("/productos/{id}")
    public String detalleProducto(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id)
                .filter(p -> !Boolean.FALSE.equals(p.getActivo()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        List<Categoria> categorias = categoriaRepository.findByActivaTrueOrderByNombreAsc();

        model.addAttribute("categoriasHeader", categorias);
        model.addAttribute("categoriaSeleccionadaId", producto.getCategoria() != null ? producto.getCategoria().getId() : null);
        model.addAttribute("busqueda", "");
        model.addAttribute("producto", producto);

        return "producto-detalle";
    }

    @GetMapping("/productos/{id}/imagen")
    public ResponseEntity<byte[]> verImagenProducto(@PathVariable Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        if (producto.getImagen() == null || producto.getImagen().length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagen no encontrada");
        }

        String tipoImagen = producto.getImagenTipo();

        if (tipoImagen == null || tipoImagen.isBlank()) {
            tipoImagen = "image/jpeg";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(tipoImagen))
                .body(producto.getImagen());
    }
}