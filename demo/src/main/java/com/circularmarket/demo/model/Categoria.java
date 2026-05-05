package com.circularmarket.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATid")
    private Long id;

    @Column(name = "CATnombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "CATslug", nullable = false, unique = true, length = 120)
    private String slug;

    @Column(name = "CATorden", nullable = false)
    private Integer orden = 0;

    @Column(name = "CATactiva", nullable = false)
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug != null ? slug.trim().toLowerCase() : null;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden != null ? orden : 0;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}