package com.circularmarket.demo.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UsuarioAutenticado implements UserDetails {

    private final Long id;
    private final String nombre;
    private final String apellidos;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UsuarioAutenticado(
            Long id,
            String nombre,
            String apellidos,
            String email,
            String password,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // ========================
    // DATOS PERSONALIZADOS
    // ========================

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getNombreCompleto() {
        if (nombre == null && apellidos == null) return "";
        if (nombre == null || nombre.isBlank()) return apellidos;
        if (apellidos == null || apellidos.isBlank()) return nombre;
        return nombre + " " + apellidos;
    }

    // ========================
    // MÉTODOS DE SPRING SECURITY
    // ========================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // Esto es lo que Spring considera "username"
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}