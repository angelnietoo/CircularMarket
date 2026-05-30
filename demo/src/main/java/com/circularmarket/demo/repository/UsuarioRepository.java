package com.circularmarket.demo.repository;

import com.circularmarket.demo.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRolNombreIgnoreCase(String nombre);

    @Query("""
            SELECT u
            FROM Usuario u
            LEFT JOIN u.rol r
            WHERE
                LOWER(u.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(u.telefono) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(u.direccion) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(u.usrol) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(r.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    Page<Usuario> buscarUsuarios(@Param("q") String q, Pageable pageable);
}