package com.circularmarket.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int LIMITE_GENERAL = 120;
    private static final int LIMITE_SENSIBLE = 20;
    private static final long VENTANA_TIEMPO_MS = 60_000;

    private final Map<String, ContadorPeticiones> peticionesPorIp = new ConcurrentHashMap<>();

    // Controla cada petición antes de que llegue al controller.
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String ruta = request.getRequestURI();

        // Los recursos estáticos no se limitan para no romper imágenes, CSS o JS.
        if (esRecursoEstatico(ruta)) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = obtenerIpCliente(request);

        // Las rutas sensibles tienen un límite más bajo que el resto de la web.
        int limite = esRutaSensible(ruta) ? LIMITE_SENSIBLE : LIMITE_GENERAL;

        // Se crea una clave por IP y tipo de ruta para contar sus peticiones.
        String clave = ip + ":" + tipoRuta(ruta);

        ContadorPeticiones contador = peticionesPorIp.computeIfAbsent(
                clave,
                k -> new ContadorPeticiones()
        );

        // Si supera el límite, se bloquea la petición con error 429.
        if (!contador.permitirPeticion(limite)) {
            response.setStatus(429);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Demasiadas peticiones. Inténtalo de nuevo en unos segundos.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // Detecta archivos estáticos que no deben contar para el límite.
    private boolean esRecursoEstatico(String ruta) {
        return ruta.startsWith("/css/")
                || ruta.startsWith("/js/")
                || ruta.startsWith("/images/")
                || ruta.startsWith("/img/")
                || ruta.equals("/favicon.ico");
    }

    // Define las rutas más delicadas de la aplicación.
    private boolean esRutaSensible(String ruta) {
        return ruta.equals("/login")
                || ruta.equals("/registro")
                || ruta.equals("/recuperar-contrasena")
                || ruta.equals("/nueva-contrasena")
                || ruta.equals("/pago/checkout")
                || ruta.startsWith("/carrito/agregar")
                || ruta.startsWith("/carrito/eliminar")
                || ruta.startsWith("/admin/");
    }

    // Separa el contador entre rutas normales y rutas sensibles.
    private String tipoRuta(String ruta) {
        return esRutaSensible(ruta) ? "sensible" : "general";
    }

    // Obtiene la IP real del usuario.
    private String obtenerIpCliente(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private static class ContadorPeticiones {

        private int contador = 0;
        private long inicioVentana = Instant.now().toEpochMilli();

        // Cuenta peticiones dentro de una ventana de tiempo.
        public synchronized boolean permitirPeticion(int limite) {
            long ahora = Instant.now().toEpochMilli();

            // Si pasa un minuto, se reinicia el contador.
            if (ahora - inicioVentana > VENTANA_TIEMPO_MS) {
                contador = 0;
                inicioVentana = ahora;
            }

            contador++;

            return contador <= limite;
        }
    }
}