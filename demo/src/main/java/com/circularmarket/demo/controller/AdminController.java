package com.circularmarket.demo.controller;

import com.circularmarket.demo.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DashboardService dashboardService;

    public AdminController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalUsuarios", dashboardService.totalUsuarios());
        model.addAttribute("totalAdmins", dashboardService.totalAdmins());

        // Mientras no existan entidades Producto y Pedido, lo dejo en 0
        model.addAttribute("totalProductos", 0);
        model.addAttribute("totalPedidos", 0);

        return "admin/dashboard";
    }
}