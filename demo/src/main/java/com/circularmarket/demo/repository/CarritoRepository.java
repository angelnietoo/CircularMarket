package com.circularmarket.demo.repository;

import com.circularmarket.demo.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuarioId(Long usuarioId);
}