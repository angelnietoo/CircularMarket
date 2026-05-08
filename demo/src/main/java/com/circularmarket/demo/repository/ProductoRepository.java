package com.circularmarket.demo.repository;

import com.circularmarket.demo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCategoria_Id(Long categoriaId);

    List<Producto> findByActivoTrueOrderByCreadoEnDesc();

    List<Producto> findTop4ByCategoria_IdAndActivoTrueOrderByCreadoEnDesc(Long categoriaId);

    @Query("""
           SELECT DISTINCT p
           FROM Producto p
           LEFT JOIN p.categoria c
           WHERE p.activo = true
           AND (
                LOWER(p.titulo) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))
                OR LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%'))

                OR (
                    (
                        LOWER(:busqueda) LIKE '%ordenador%'
                        OR LOWER(:busqueda) LIKE '%ordenadores%'
                        OR LOWER(:busqueda) LIKE '%pc%'
                        OR LOWER(:busqueda) LIKE '%portatil%'
                        OR LOWER(:busqueda) LIKE '%portátil%'
                        OR LOWER(:busqueda) LIKE '%laptop%'
                        OR LOWER(:busqueda) LIKE '%informatica%'
                        OR LOWER(:busqueda) LIKE '%informática%'
                        OR LOWER(:busqueda) LIKE '%tecnologia%'
                        OR LOWER(:busqueda) LIKE '%tecnología%'
                    )
                    AND (
                        LOWER(c.nombre) LIKE '%informatica%'
                        OR LOWER(c.nombre) LIKE '%informática%'
                        OR LOWER(c.nombre) LIKE '%oficina%'
                        OR LOWER(c.descripcion) LIKE '%informatica%'
                        OR LOWER(c.descripcion) LIKE '%informática%'
                        OR LOWER(c.descripcion) LIKE '%oficina%'
                    )
                )

                OR (
                    (
                        LOWER(:busqueda) LIKE '%movil%'
                        OR LOWER(:busqueda) LIKE '%móvil%'
                        OR LOWER(:busqueda) LIKE '%telefono%'
                        OR LOWER(:busqueda) LIKE '%teléfono%'
                        OR LOWER(:busqueda) LIKE '%smartphone%'
                        OR LOWER(:busqueda) LIKE '%tablet%'
                        OR LOWER(:busqueda) LIKE '%electronica%'
                        OR LOWER(:busqueda) LIKE '%electrónica%'
                    )
                    AND (
                        LOWER(c.nombre) LIKE '%electronica%'
                        OR LOWER(c.nombre) LIKE '%electrónica%'
                        OR LOWER(c.descripcion) LIKE '%electronica%'
                        OR LOWER(c.descripcion) LIKE '%electrónica%'
                    )
                )

                OR (
                    (
                        LOWER(:busqueda) LIKE '%ropa%'
                        OR LOWER(:busqueda) LIKE '%camiseta%'
                        OR LOWER(:busqueda) LIKE '%pantalon%'
                        OR LOWER(:busqueda) LIKE '%pantalón%'
                        OR LOWER(:busqueda) LIKE '%zapatos%'
                        OR LOWER(:busqueda) LIKE '%zapatillas%'
                        OR LOWER(:busqueda) LIKE '%moda%'
                    )
                    AND (
                        LOWER(c.nombre) LIKE '%moda%'
                        OR LOWER(c.nombre) LIKE '%ropa%'
                        OR LOWER(c.descripcion) LIKE '%moda%'
                        OR LOWER(c.descripcion) LIKE '%ropa%'
                    )
                )

                OR (
                    (
                        LOWER(:busqueda) LIKE '%casa%'
                        OR LOWER(:busqueda) LIKE '%hogar%'
                        OR LOWER(:busqueda) LIKE '%mueble%'
                        OR LOWER(:busqueda) LIKE '%muebles%'
                        OR LOWER(:busqueda) LIKE '%decoracion%'
                        OR LOWER(:busqueda) LIKE '%decoración%'
                    )
                    AND (
                        LOWER(c.nombre) LIKE '%hogar%'
                        OR LOWER(c.nombre) LIKE '%casa%'
                        OR LOWER(c.nombre) LIKE '%decoracion%'
                        OR LOWER(c.nombre) LIKE '%decoración%'
                        OR LOWER(c.descripcion) LIKE '%hogar%'
                        OR LOWER(c.descripcion) LIKE '%casa%'
                        OR LOWER(c.descripcion) LIKE '%decoracion%'
                        OR LOWER(c.descripcion) LIKE '%decoración%'
                    )
                )

                OR (
                    (
                        LOWER(:busqueda) LIKE '%gaming%'
                        OR LOWER(:busqueda) LIKE '%juego%'
                        OR LOWER(:busqueda) LIKE '%juegos%'
                        OR LOWER(:busqueda) LIKE '%consola%'
                        OR LOWER(:busqueda) LIKE '%consolas%'
                        OR LOWER(:busqueda) LIKE '%ps5%'
                        OR LOWER(:busqueda) LIKE '%xbox%'
                        OR LOWER(:busqueda) LIKE '%nintendo%'
                    )
                    AND (
                        LOWER(c.nombre) LIKE '%gaming%'
                        OR LOWER(c.nombre) LIKE '%juego%'
                        OR LOWER(c.nombre) LIKE '%juegos%'
                        OR LOWER(c.nombre) LIKE '%consola%'
                        OR LOWER(c.descripcion) LIKE '%gaming%'
                        OR LOWER(c.descripcion) LIKE '%juego%'
                        OR LOWER(c.descripcion) LIKE '%juegos%'
                        OR LOWER(c.descripcion) LIKE '%consola%'
                    )
                )
           )
           ORDER BY p.creadoEn DESC
           """)
    List<Producto> buscarProductosActivos(@Param("busqueda") String busqueda);
}