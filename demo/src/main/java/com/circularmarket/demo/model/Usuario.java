package com.circularmarket.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USid")
    private Long id;

    // ===== IDENTIDAD =====
    @Column(name = "USnombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "USapellidos", nullable = false, length = 150)
    private String apellidos;

    // ===== CREDENCIALES =====
    @Column(name = "UScorreo", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "UScontrasena", nullable = false, length = 255)
    private String contrasena;

    @Column(name = "USrol", nullable = false)
    private String rol;

    // ===== CONTACTO =====
    @Column(name = "UStelefono", length = 30)
    private String telefono;

    // ===== AUDITORÍA =====
    @Column(name = "UScreadoen", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "USactualizadoen", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    public Usuario() {}

    // GETTERS Y SETTERS

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

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }
}