package com.circularmarket.demo.controller;

import com.circularmarket.demo.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    private final DashboardService dashboardService;

    public AdminController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // Muestra el dashboard principal del panel de administración.
    @GetMapping("/admin")
    public String dashboard(Model model) {
        // Carga los contadores principales.
        model.addAttribute("totalUsuarios", dashboardService.totalUsuarios());
        model.addAttribute("totalAdmins", dashboardService.totalAdmins());
        model.addAttribute("totalProductos", dashboardService.totalProductos());
        model.addAttribute("totalPedidos", dashboardService.totalPedidos());

        // Carga los pedidos pendientes.
        model.addAttribute("pedidosPendientes", dashboardService.pedidosPendientes());

        return "admin/dashboard";
    }

    // Cambia el estado de un pedido pendiente a entregado.
    @PostMapping("/admin/pedidos/{id}/entregar")
    public String marcarPedidoComoEntregado(@PathVariable Long id) {
        dashboardService.marcarPedidoComoEntregado(id);

        // Vuelve al dashboard.
        return "redirect:/admin";
    }

    // Elimina un pedido desde el panel de administración.
    @PostMapping("/admin/pedidos/{id}/eliminar")
    public String eliminarPedido(@PathVariable Long id) {
        dashboardService.eliminarPedido(id);

        // Vuelve al dashboard.
        return "redirect:/admin";
    }
}