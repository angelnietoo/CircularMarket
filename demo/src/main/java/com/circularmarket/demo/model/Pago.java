package com.circularmarket.demo.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAid", nullable = false)
    private Long id;

    @Column(name = "PApedidoid", nullable = true)
    private Long pedidoId;

    @Column(name = "PAimporte", nullable = false, precision = 12, scale = 2)
    private BigDecimal importe;

    @Column(name = "PAmetodo", nullable = false, length = 50)
    private String metodo;

    @Column(name = "PAestadopago", nullable = false, length = 50)
    private String estadoPago;

    @Column(name = "PAtransaccionid", nullable = false, unique = true, length = 255)
    private String transaccionId;

    @Column(name = "PApagadoen")
    private LocalDateTime pagadoEn;

    @Column(name = "PAcreadoen")
    private LocalDateTime creadoEn;

    public Pago() {
    }

    @PrePersist
    public void prePersist() {
        if (this.creadoEn == null) {
            this.creadoEn = LocalDateTime.now();
        }

        if (this.pagadoEn == null && this.estadoPago != null && "PAGADO".equalsIgnoreCase(this.estadoPago)) {
            this.pagadoEn = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getTransaccionId() {
        return transaccionId;
    }

    public void setTransaccionId(String transaccionId) {
        this.transaccionId = transaccionId;
    }

    public LocalDateTime getPagadoEn() {
        return pagadoEn;
    }

    public void setPagadoEn(LocalDateTime pagadoEn) {
        this.pagadoEn = pagadoEn;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}