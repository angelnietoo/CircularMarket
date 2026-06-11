package com.circularmarket.demo.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

// Representa al usuario que ha iniciado sesión dentro de Spring Security.
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

    // Devuelve el ID del usuario autenticado.
    public Long getId() {
        return id;
    }

    // Devuelve el nombre del usuario.
    public String getNombre() {
        return nombre;
    }

    // Devuelve los apellidos.
    public String getApellidos() {
        return apellidos;
    }

    // Devuelve el nombre completo.
    public String getNombreCompleto() {
        // Si no hay nombre ni apellidos, devuelve texto vacío.
        if (nombre == null && apellidos == null) return "";

        // Si no hay nombre, devuelve solo apellidos.
        if (nombre == null || nombre.isBlank()) return apellidos;

        // Si no hay apellidos, devuelve nombre.
        if (apellidos == null || apellidos.isBlank()) return nombre;

        // Si existen ambos, une nombre y apellidos.
        return nombre + " " + apellidos;
    }

    // Devuelve los permisos o roles del usuario.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Devuelve la contraseña cifrada.
    @Override
    public String getPassword() {
        return password;
    }

    // Devuelve el email, que se usa como nombre de usuario para iniciar sesión.
    @Override
    public String getUsername() {
        return email;
    }

    // Indica si la cuenta no ha caducado.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Indica si la cuenta no está bloqueada.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Indica si la contraseña no ha caducado.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Indica si la cuenta está activa.
    @Override
    public boolean isEnabled() {
        return true;
    }
}