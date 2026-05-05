package com.circularmarket.demo.service;

import com.circularmarket.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UsuarioRepository usuarioRepository;

    public DashboardService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public long totalUsuarios() {
        return usuarioRepository.count();
    }

    public long totalAdmins() {
        return usuarioRepository.countByRol_NombreIgnoreCase("admin");
    }
}