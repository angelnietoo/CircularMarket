package com.circularmarket.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rolusuarios")
public class RolUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RUid")
    private Long id;

    @Column(name = "RUnombre", nullable = false, unique = true, length = 50)
    private String nombre;

    public RolUsuario() {
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre != null ? nombre.trim().toUpperCase() : null;
    }
}