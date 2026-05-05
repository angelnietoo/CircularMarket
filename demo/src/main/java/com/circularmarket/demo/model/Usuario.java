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

    @Column(name = "USnombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "USapellidos", nullable = false, length = 150)
    private String apellidos;

    @Column(name = "UScorreo", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "UScontrasena", nullable = false, length = 255)
    private String contrasena;

    @ManyToOne
    @JoinColumn(name = "RUid")
    private RolUsuario rol;

    @Column(name = "usrol", nullable = false, length = 50)
    private String usrol;

    @Column(name = "UStelefono", length = 30)
    private String telefono;

    @Column(name = "USdireccion", length = 255)
    private String direccion;

    @Column(name = "UScreadoen", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "USactualizadoen", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    public Usuario() {}

    // ========================
    // GETTERS Y SETTERS
    // ========================

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre != null ? nombre.trim() : null;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos != null ? apellidos.trim() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    // 🔥 CAMBIO: ahora devuelve RolUsuario
    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
        this.usrol = rol != null ? rol.getNombre() : null;
    }

    public String getUsrol() {
        return usrol;
    }

    public void setUsrol(String usrol) {
        this.usrol = usrol != null ? usrol.trim() : null;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono != null ? telefono.trim() : null;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion != null ? direccion.trim() : null;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    // ========================
    // MÉTODOS ÚTILES
    // ========================

    public String getNombreCompleto() {
        if (nombre == null && apellidos == null) return "";
        if (nombre == null || nombre.isBlank()) return apellidos;
        if (apellidos == null || apellidos.isBlank()) return nombre;
        return nombre + " " + apellidos;
    }
}