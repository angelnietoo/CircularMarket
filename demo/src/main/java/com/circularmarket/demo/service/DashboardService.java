package com.circularmarket.demo.service;

import com.circularmarket.demo.model.EstadoPedido;
import com.circularmarket.demo.model.Pedido;
import com.circularmarket.demo.repository.PedidoRepository;
import com.circularmarket.demo.repository.ProductoRepository;
import com.circularmarket.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;

    public DashboardService(UsuarioRepository usuarioRepository,
                            ProductoRepository productoRepository,
                            PedidoRepository pedidoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public long totalUsuarios() {
        return usuarioRepository.count();
    }

    public long totalAdmins() {
        return usuarioRepository.countByRolNombreIgnoreCase("admin");
    }

    public long totalProductos() {
        return productoRepository.count();
    }

    public long totalPedidos() {
        return pedidoRepository.count();
    }

    public List<Pedido> pedidosPendientes() {
        return pedidoRepository.findByEstadoOrderByFechaPedidoDesc(EstadoPedido.pendiente);
    }

    @Transactional
    public void marcarPedidoComoEntregado(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el pedido con ID: " + id));

        // Si existe se marca como entregado
        pedido.setEstado(EstadoPedido.entregado);
    }

    @Transactional
    public void eliminarPedido(Long id) {
        if (!pedidoRepository.existsById(id)) {
            return;
        }

        // Si existe se elimina
        pedidoRepository.deleteById(id);
    }
}