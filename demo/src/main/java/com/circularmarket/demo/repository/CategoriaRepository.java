package com.circularmarket.demo.repository;

import com.circularmarket.demo.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByActivaTrueOrderByNombreAsc();

    Optional<Categoria> findByNombreAndActivaTrue(String nombre);
}