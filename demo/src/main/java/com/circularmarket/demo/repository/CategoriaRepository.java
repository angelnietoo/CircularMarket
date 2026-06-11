package com.circularmarket.demo.repository;

import com.circularmarket.demo.model.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByActivaTrueOrderByNombreAsc();

    @Query("""
            SELECT c
            FROM Categoria c
            WHERE
                LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                OR LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                OR LOWER(CAST(c.id AS string)) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                OR (
                    LOWER(:busqueda) IN ('activa', 'activo')
                    AND c.activa = true
                )
                OR (
                    LOWER(:busqueda) IN ('inactiva', 'inactivo')
                    AND c.activa = false
                )
            """)
    Page<Categoria> buscarCategoriasAdmin(@Param("busqueda") String busqueda, Pageable pageable);
}