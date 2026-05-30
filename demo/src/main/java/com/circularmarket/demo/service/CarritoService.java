package com.circularmarket.demo.service;

import com.circularmarket.demo.dto.CarritoItem;
import com.circularmarket.demo.model.Carrito;
import com.circularmarket.demo.model.ItemCarrito;
import com.circularmarket.demo.model.Producto;
import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.CarritoRepository;
import com.circularmarket.demo.repository.ItemCarritoRepository;
import com.circularmarket.demo.repository.ProductoRepository;
import com.circularmarket.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarritoService(CarritoRepository carritoRepository,
                          ItemCarritoRepository itemCarritoRepository,
                          ProductoRepository productoRepository,
                          UsuarioRepository usuarioRepository) {
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Carrito obtenerOCrearCarrito(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }

        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepository.findById(usuarioId)
                            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

                    Carrito carrito = new Carrito();
                    carrito.setUsuario(usuario);

                    return carritoRepository.save(carrito);
                });
    }

    @Transactional
    public void anadirProducto(Long usuarioId, Long productoId) {
        if (usuarioId == null || productoId == null) {
            return;
        }

        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        Producto producto = productoRepository.findById(productoId).orElse(null);

        if (producto == null || Boolean.FALSE.equals(producto.getActivo())) {
            return;
        }

        int stock = producto.getStock() != null ? producto.getStock() : 0;

        if (stock <= 0) {
            return;
        }

        ItemCarrito item = itemCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), producto.getId())
                .orElse(null);

        if (item == null) {
            item = new ItemCarrito();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setCantidad(1);

            itemCarritoRepository.save(item);
            return;
        }

        int cantidadActual = item.getCantidad() != null ? item.getCantidad() : 1;

        if (cantidadActual < stock) {
            item.setCantidad(cantidadActual + 1);
            itemCarritoRepository.save(item);
        }
    }

    @Transactional
    public void aumentarCantidad(Long usuarioId, Long productoId) {
        anadirProducto(usuarioId, productoId);
    }

    @Transactional
    public void disminuirCantidad(Long usuarioId, Long productoId) {
        if (usuarioId == null || productoId == null) {
            return;
        }

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId).orElse(null);

        if (carrito == null) {
            return;
        }

        ItemCarrito item = itemCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElse(null);

        if (item == null) {
            return;
        }

        int cantidadActual = item.getCantidad() != null ? item.getCantidad() : 1;

        if (cantidadActual <= 1) {
            itemCarritoRepository.delete(item);
            return;
        }

        item.setCantidad(cantidadActual - 1);
        itemCarritoRepository.save(item);
    }

    @Transactional
    public void eliminarProducto(Long usuarioId, Long productoId) {
        if (usuarioId == null || productoId == null) {
            return;
        }

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId).orElse(null);

        if (carrito == null) {
            return;
        }

        itemCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .ifPresent(itemCarritoRepository::delete);
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        if (usuarioId == null) {
            return;
        }

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId).orElse(null);

        if (carrito == null) {
            return;
        }

        itemCarritoRepository.deleteByCarritoId(carrito.getId());
    }

    @Transactional(readOnly = true)
    public List<CarritoItem> obtenerItems(Long usuarioId) {
        if (usuarioId == null) {
            return new ArrayList<>();
        }

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId).orElse(null);

        if (carrito == null) {
            return new ArrayList<>();
        }

        List<ItemCarrito> itemsBD = itemCarritoRepository.findByCarritoId(carrito.getId());
        List<CarritoItem> carritoItems = new ArrayList<>();

        for (ItemCarrito itemBD : itemsBD) {
            Producto producto = itemBD.getProducto();

            if (producto == null || Boolean.FALSE.equals(producto.getActivo())) {
                continue;
            }

            if (producto.getPrecio() == null) {
                continue;
            }

            String imagenUrl = producto.getImagen() != null && producto.getImagen().length > 0
                    ? "/productos/" + producto.getId() + "/imagen"
                    : null;

            CarritoItem item = new CarritoItem(
                    producto.getId(),
                    producto.getTitulo(),
                    producto.getDescripcion(),
                    imagenUrl,
                    producto.getPrecio(),
                    producto.getStock(),
                    itemBD.getCantidad()
            );

            carritoItems.add(item);
        }

        return carritoItems;
    }

    @Transactional(readOnly = true)
    public int contarItems(Long usuarioId) {
        List<CarritoItem> items = obtenerItems(usuarioId);

        int total = 0;

        for (CarritoItem item : items) {
            total += item.getCantidad() != null ? item.getCantidad() : 0;
        }

        return total;
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularSubtotal(Long usuarioId) {
        List<CarritoItem> items = obtenerItems(usuarioId);

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CarritoItem item : items) {
            subtotal = subtotal.add(item.getTotal());
        }

        return subtotal;
    }
}