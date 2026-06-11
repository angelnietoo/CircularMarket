package com.circularmarket.demo.controller;

import com.circularmarket.demo.dto.CarritoItem;
import com.circularmarket.demo.model.Categoria;
import com.circularmarket.demo.model.EstadoPedido;
import com.circularmarket.demo.model.Pago;
import com.circularmarket.demo.model.Pedido;
import com.circularmarket.demo.model.Usuario;
import com.circularmarket.demo.repository.CategoriaRepository;
import com.circularmarket.demo.repository.PagoRepository;
import com.circularmarket.demo.repository.PedidoRepository;
import com.circularmarket.demo.repository.UsuarioRepository;
import com.circularmarket.demo.service.CarritoService;
import com.circularmarket.demo.service.UsuarioAutenticado;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/pago")
public class PagoController {

    private static final BigDecimal GASTOS_ENVIO = BigDecimal.valueOf(4.99);

    private final CategoriaRepository categoriaRepository;
    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoService carritoService;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${app.base-url}")
    private String appBaseUrl;

    public PagoController(CategoriaRepository categoriaRepository,
                          PagoRepository pagoRepository,
                          PedidoRepository pedidoRepository,
                          UsuarioRepository usuarioRepository,
                          CarritoService carritoService) {
        this.categoriaRepository = categoriaRepository;
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.carritoService = carritoService;
    }

    // Crea una sesión de pago en Stripe con los productos del carrito.
    @PostMapping("/stripe")
    public String pagarConStripe(Authentication authentication) throws StripeException {
        Long usuarioId = obtenerUsuarioIdAutenticado(authentication);

        // Si no hay usuario autenticado, se redirige al login.
        if (usuarioId == null) {
            return "redirect:/login";
        }

        List<CarritoItem> carritoItems = carritoService.obtenerItems(usuarioId);

        // Si el carrito está vacío, no se inicia el pago.
        if (carritoItems == null || carritoItems.isEmpty()) {
            return "redirect:/carrito";
        }

        // Configura la clave privada de Stripe.
        Stripe.apiKey = stripeSecretKey;

        // Prepara la sesión de checkout con URL de éxito y cancelación.
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(appBaseUrl + "/pago/exito?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(appBaseUrl + "/pago/cancelado");

        boolean hayProductosValidos = false;

        // Recorre los productos del carrito para añadirlos al checkout.
        for (CarritoItem item : carritoItems) {
            // Ignora productos incompletos o con cantidad incorrecta.
            if (item == null || item.getPrecio() == null || item.getCantidad() == null || item.getCantidad() < 1) {
                continue;
            }

            String nombreProducto = item.getNombre();

            if (nombreProducto == null || nombreProducto.isBlank()) {
                nombreProducto = "Producto";
            }

            long precioEnCentimos = convertirEurosACentimos(item.getPrecio());

            // Stripe trabaja en céntimos, por eso se descartan importes no válidos.
            if (precioEnCentimos <= 0) {
                continue;
            }

            // Crea la información del producto que verá el usuario en Stripe.
            SessionCreateParams.LineItem.PriceData.ProductData.Builder productDataBuilder =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(nombreProducto);

            if (item.getDescripcion() != null && !item.getDescripcion().isBlank()) {
                productDataBuilder.setDescription(item.getDescripcion());
            }

            // Define el precio unitario del producto.
            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("eur")
                            .setUnitAmount(precioEnCentimos)
                            .setProductData(productDataBuilder.build())
                            .build();

            // Añade el producto con su cantidad al checkout.
            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(Long.valueOf(item.getCantidad()))
                            .setPriceData(priceData)
                            .build();

            paramsBuilder.addLineItem(lineItem);
            hayProductosValidos = true;
        }

        // Si no se pudo añadir ningún producto válido, vuelve al carrito.
        if (!hayProductosValidos) {
            return "redirect:/carrito";
        }

        // Añade los gastos de envío como una línea más del pago.
        SessionCreateParams.LineItem.PriceData envioPriceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("eur")
                        .setUnitAmount(convertirEurosACentimos(GASTOS_ENVIO))
                        .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Gastos de envío")
                                        .build()
                        )
                        .build();

        SessionCreateParams.LineItem envioLineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(envioPriceData)
                        .build();

        paramsBuilder.addLineItem(envioLineItem);

        // Crea la sesión real de Stripe.
        Session stripeSession = Session.create(paramsBuilder.build());

        // Si Stripe no devuelve URL, se considera pago cancelado.
        if (stripeSession.getUrl() == null || stripeSession.getUrl().isBlank()) {
            return "redirect:/pago/cancelado";
        }

        // Redirige al usuario al checkout de Stripe.
        return "redirect:" + stripeSession.getUrl();
    }

    // Gestiona la vuelta desde Stripe cuando el pago ha sido correcto.
    @GetMapping("/exito")
    @Transactional
    public String pagoExitoso(@RequestParam(name = "session_id", required = false) String sessionId,
                              Model model,
                              Authentication authentication) {

        // Sin session_id no se puede comprobar el pago.
        if (sessionId == null || sessionId.isBlank()) {
            return "redirect:/pago/cancelado";
        }

        Long usuarioId = obtenerUsuarioIdAutenticado(authentication);

        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            Stripe.apiKey = stripeSecretKey;

            // Recupera la sesión de Stripe para comprobar el estado real del pago.
            Session stripeSession = Session.retrieve(sessionId);

            // Solo continúa si Stripe indica que el pago está pagado.
            if (!"paid".equalsIgnoreCase(stripeSession.getPaymentStatus())) {
                return "redirect:/pago/cancelado";
            }

            // Guarda el pedido y el pago si todavía no existen.
            guardarPedidoYPagoSiNoExiste(stripeSession, usuarioId);

            // Vacía el carrito después de completar la compra.
            carritoService.vaciarCarrito(usuarioId);

            cargarDatosHeader(model);
            model.addAttribute("carritoCantidad", 0);
            model.addAttribute("sessionId", sessionId);

            return "checkout-exito";

        } catch (StripeException e) {
            return "redirect:/pago/cancelado";
        }
    }

    // Muestra la vista de pago cancelado.
    @GetMapping("/cancelado")
    public String pagoCancelado(Model model) {
        cargarDatosHeader(model);
        return "checkout-cancelado";
    }

    // Guarda el pedido y el pago en la base de datos evitando duplicados.
    private void guardarPedidoYPagoSiNoExiste(Session stripeSession, Long usuarioId) {
        String transaccionId = stripeSession.getId();

        if (transaccionId == null || transaccionId.isBlank()) {
            return;
        }

        // Si ya existe un pago con esa transacción, no se vuelve a guardar.
        if (pagoRepository.existsByTransaccionId(transaccionId)) {
            return;
        }

        BigDecimal importe = BigDecimal.ZERO;

        // Stripe devuelve el total en céntimos, se convierte a euros.
        if (stripeSession.getAmountTotal() != null) {
            importe = BigDecimal.valueOf(stripeSession.getAmountTotal())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        // Crea el pedido asociado al usuario.
        Pedido pedido = new Pedido();
        pedido.setCompradorId(usuarioId.intValue());
        pedido.setDireccionEnvioId(null);
        pedido.setImporteTotal(importe);

        /*
         * PEestado representa el estado del pedido:
         * - pendiente
         * - entregado
         *
         * El pago se guarda aparte en pagos.PAestadopago.
         */
        pedido.setEstado(EstadoPedido.pendiente);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Crea el registro del pago asociado al pedido.
        Pago pago = new Pago();
        pago.setPedidoId(pedidoGuardado.getId());
        pago.setImporte(importe);
        pago.setMetodo("STRIPE");
        pago.setEstadoPago("PAGADO");
        pago.setTransaccionId(transaccionId);
        pago.setPagadoEn(LocalDateTime.now());

        pagoRepository.save(pago);
    }

    // Obtiene el ID del usuario autenticado según el tipo de login usado.
    private Long obtenerUsuarioIdAutenticado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // Caso de login normal con usuario propio de la aplicación.
        if (principal instanceof UsuarioAutenticado usuarioAutenticado) {
            return usuarioAutenticado.getId();
        }

        // Caso de login con Google OAuth2.
        if (principal instanceof OAuth2User oauth2User) {
            Object emailObject = oauth2User.getAttribute("email");

            if (emailObject == null || emailObject.toString().isBlank()) {
                return null;
            }

            String email = emailObject.toString().trim().toLowerCase();

            return usuarioRepository.findByEmail(email)
                    .map(Usuario::getId)
                    .orElse(null);
        }

        // Caso alternativo usando el email guardado en la autenticación.
        String email = authentication.getName();

        if (email == null || email.isBlank()) {
            return null;
        }

        return usuarioRepository.findByEmail(email.trim().toLowerCase())
                .map(Usuario::getId)
                .orElse(null);
    }

    // Convierte un importe en euros a céntimos para Stripe.
    private long convertirEurosACentimos(BigDecimal importe) {
        if (importe == null) {
            return 0L;
        }

        return importe
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }

    // Carga los datos necesarios para mostrar el header.
    private void cargarDatosHeader(Model model) {
        List<Categoria> categorias = categoriaRepository.findByActivaTrueOrderByNombreAsc();

        model.addAttribute("categoriasHeader", categorias);
        model.addAttribute("categoriaSeleccionadaId", null);
        model.addAttribute("busqueda", "");
    }
}