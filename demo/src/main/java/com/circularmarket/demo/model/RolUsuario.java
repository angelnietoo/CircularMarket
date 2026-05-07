package com.circularmarket.demo.model;

import jakarta.persistence.*;
<<<<<<< HEAD
=======
import java.util.Objects;
>>>>>>> bbf15444231fb4b62d5f4a4c11e53092aa22254a

@Entity
@Table(name = "rolusuarios")
public class RolUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RUid")
    private Long id;

<<<<<<< HEAD
    @Column(name = "RUnombre", nullable = false, unique = true, length = 50)
    private String nombre;

    public RolUsuario() {
    }

=======
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
>>>>>>> bbf15444231fb4b62d5f4a4c11e53092aa22254a
    public Long getId() {
        return id;
    }

<<<<<<< HEAD
=======
    public void setId(Long id) {
        this.id = id;
    }

>>>>>>> bbf15444231fb4b62d5f4a4c11e53092aa22254a
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
<<<<<<< HEAD
        this.nombre = nombre != null ? nombre.trim().toUpperCase() : null;
=======
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
>>>>>>> bbf15444231fb4b62d5f4a4c11e53092aa22254a
    }
}