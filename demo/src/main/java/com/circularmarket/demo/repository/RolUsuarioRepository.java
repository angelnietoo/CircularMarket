package com.circularmarket.demo.repository;

import com.circularmarket.demo.model.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Long> {
    Optional<RolUsuario> findByNombre(String nombre);
}