package com.circularmarket.demo.repository;

import com.circularmarket.demo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCategoria_Id(Long categoriaId);
}