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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RUid")
    private RolUsuario rol;

    @Column(name = "usrol", nullable = false, length = 20)
    private String usrol;

    @Column(name = "UStelefono", length = 30)
    private String telefono;

    @Column(name = "USdireccion", length = 255)
    private String direccion;

    @Column(name = "UScreadoen", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "USactualizadoen", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    @Column(name = "USreset_token", length = 120)
    private String resetToken;

    @Column(name = "USreset_token_expira")
    private LocalDateTime resetTokenExpira;

    public Usuario() {
    }

    @PrePersist
    @PreUpdate
    private void sincronizarRolTexto() {
        if (rol != null && rol.getNombre() != null && !rol.getNombre().isBlank()) {
            this.usrol = rol.getNombre().trim().toUpperCase();
        }

        if (this.usrol == null || this.usrol.isBlank()) {
            this.usrol = "USER";
        }
    }

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

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;

        if (rol != null && rol.getNombre() != null) {
            this.usrol = rol.getNombre().trim().toUpperCase();
        }
    }

    public String getUsrol() {
        return usrol;
    }

    public void setUsrol(String usrol) {
        this.usrol = usrol != null ? usrol.trim().toUpperCase() : null;
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

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpira() {
        return resetTokenExpira;
    }

    public void setResetTokenExpira(LocalDateTime resetTokenExpira) {
        this.resetTokenExpira = resetTokenExpira;
    }

    public String getNombreCompleto() {
        if (nombre == null && apellidos == null) return "";
        if (nombre == null || nombre.isBlank()) return apellidos;
        if (apellidos == null || apellidos.isBlank()) return nombre;

        return nombre + " " + apellidos;
    }
}