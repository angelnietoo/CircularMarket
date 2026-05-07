package com.circularmarket.demo.repository;

import com.circularmarket.demo.model.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< HEAD
public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Long> {
=======
import java.util.Optional;

public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Long> {
    Optional<RolUsuario> findByNombre(String nombre);
>>>>>>> bbf15444231fb4b62d5f4a4c11e53092aa22254a
}