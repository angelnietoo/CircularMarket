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

        // Busca el carrito del usuario. Si no existe, crea uno nuevo.
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

        // Si el producto no existe o no está activo, no se añade al carrito.
        if (producto == null || Boolean.FALSE.equals(producto.getActivo())) {
            return;
        }

        int stock = producto.getStock() != null ? producto.getStock() : 0;

        // Si no hay stock disponible, no se añade el producto.
        if (stock <= 0) {
            return;
        }

        ItemCarrito item = itemCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), producto.getId())
                .orElse(null);

        // Si el producto todavía no está en el carrito, se añade con cantidad 1.
        if (item == null) {
            item = new ItemCarrito();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setCantidad(1);

            itemCarritoRepository.save(item);
            return;
        }

        int cantidadActual = item.getCantidad() != null ? item.getCantidad() : 1;

        // Si el producto ya está en el carrito, aumenta la cantidad sin superar el stock.
        if (cantidadActual < stock) {
            item.setCantidad(cantidadActual + 1);
            itemCarritoRepository.save(item);
        }
    }

    @Transactional
    public void aumentarCantidad(Long usuarioId, Long productoId) {
        // Usa la misma lógica que añadir un producto al carrito.
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

        // Si solo queda una unidad, se elimina el producto del carrito.
        if (cantidadActual <= 1) {
            itemCarritoRepository.delete(item);
            return;
        }

        // Si hay más de una unidad, solo se baja la cantidad en 1.
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

        // Elimina directamente el producto del carrito si existe.
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

        // Borra todos los productos del carrito.
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

            // No muestra productos eliminados, desactivados o sin precio.
            if (producto == null || Boolean.FALSE.equals(producto.getActivo())) {
                continue;
            }

            if (producto.getPrecio() == null) {
                continue;
            }

            // Si el producto tiene imagen, se crea la ruta para mostrarla en la web.
            String imagenUrl = producto.getImagen() != null && producto.getImagen().length > 0
                    ? "/productos/" + producto.getId() + "/imagen"
                    : null;

            // Convierte el item de la base de datos en un objeto más cómodo para la vista.
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

        // Suma todas las cantidades para saber cuántos productos hay en el carrito.
        for (CarritoItem item : items) {
            total += item.getCantidad() != null ? item.getCantidad() : 0;
        }

        return total;
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularSubtotal(Long usuarioId) {
        List<CarritoItem> items = obtenerItems(usuarioId);

        BigDecimal subtotal = BigDecimal.ZERO;

        // Suma el precio total de todos los productos del carrito.
        for (CarritoItem item : items) {
            subtotal = subtotal.add(item.getTotal());
        }

        return subtotal;
    }
}