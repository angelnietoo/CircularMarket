package com.circularmarket.demo.repository;

import com.circularmarket.demo.model.EstadoPedido;
import com.circularmarket.demo.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstadoOrderByFechaPedidoDesc(EstadoPedido estado);

    List<Pedido> findByCompradorIdOrderByFechaPedidoDesc(Integer compradorId);
}