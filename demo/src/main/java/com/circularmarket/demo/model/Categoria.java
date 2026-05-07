package com.circularmarket.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categoriaproductos")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CPid")
    private Long id;

    @Column(name = "CPnombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "CPdescripcion", length = 255)
    private String descripcion;

    @Column(name = "CPactiva", nullable = false)
    private boolean activa = true;

    public Categoria() {
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre != null ? nombre.trim() : null;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion != null ? descripcion.trim() : null;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}