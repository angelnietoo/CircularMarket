package com.circularmarket.demo.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "rolusuarios")
public class RolUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RUid")
    private Long id;

    @Column(name = "runombre", nullable = false, unique = true)
    private String nombre;

    // Constructor vacío
    public RolUsuario() {
    }

    // Constructor con campos
    public RolUsuario(String nombre) {
        this.nombre = nombre;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // equals y hashCode (importante en relaciones JPA)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolUsuario)) return false;
        RolUsuario that = (RolUsuario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Para debug
    @Override
    public String toString() {
        return nombre;
    }
}