package com.circularmarket.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USid")
    private Long id;

    @Column(name = "USnombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "USapellidos", nullable = false, length = 150)
    private String apellidos;

    @Column(name = "USemail", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "USclave", nullable = false, length = 255)
    private String clave;

    @Column(name = "USfecharegistro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "USrol", nullable = false, length = 50)
    private String rol;

    public Usuario() {
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}